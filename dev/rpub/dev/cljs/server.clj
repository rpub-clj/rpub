(ns rpub.dev.cljs.server
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [nrepl.middleware :as nrepl-middleware]
            [nrepl.misc :as nrepl-misc]
            [nrepl.server :as nrepl-server]
            [nrepl.transport :as transport]
            [ring.adapter.jetty9 :as jetty]
            [ring.websocket :as ws]
            [rpub.lib.transit :as transit]))

(def ws-responses (atom {}))
(def ws-clients (atom #{}))

(defn- ws-handler [request]
  (assert (ws/upgrade-request? request))
  {::ws/listener
   {:on-open (fn [socket]
               (swap! ws-clients conj socket)
               (println "WebSocket client connected"))
    :on-close (fn [socket _code _reason]
                (swap! ws-clients disj socket)
                (println "WebSocket client disconnected"))
    :on-message (fn [_socket text]
                  (let [msg (transit/read text)]
                    (deliver (get @ws-responses (:id msg)) msg)
                    (swap! ws-responses #(dissoc % (:id msg)))))}})

(def cursive-commands
  #{"(try (clojure.core/cond (clojure.core/resolve"
    "(get *compiler-options* :disable-locals-clearing)"
    "(do (clojure.core/println (clojure.core/str \"Clojure \""
    "(cursive.repl.runtime/completions"
    "(try (clojure.lang.Compiler/load (java.io.StringReader."
    "42"})

(defn- cljs? [{:keys [op code] :as _msg}]
  (and (= op "eval")
       (not (some #(str/starts-with? code %) cursive-commands))))

(defn- handle-cljs [{:keys [transport] :as msg}]
  (if-let [clients (seq @ws-clients)]
    (let [response (promise)]
      (doseq [client clients]
        (swap! ws-responses #(assoc % (:id msg) response))
        (ws/send client (transit/write (select-keys msg [:id :code]))))
      (transport/send
        transport
        (nrepl-misc/response-for msg :value (:value @response)))
      (transport/send
        transport
        (nrepl-misc/response-for msg :status :done)))
    (let [error (ex-info "No browser REPL clients connected" {})]
      (transport/send
        transport
        (nrepl-misc/response-for msg :value (pr-str error)))
      (transport/send
        transport
        (nrepl-misc/response-for msg :status :done)))))

(defn- websocket-eval-middleware [handler]
  (fn [msg]
    (if (cljs? msg)
      (handle-cljs msg)
      (handler msg))))

(nrepl-middleware/set-descriptor!
  #'websocket-eval-middleware
  {:requires #{"clone"}, :expects #{"eval"}})

(defn start-websocket-server! []
  (log/info "Starting WebSocket server on port 7778")
  (jetty/run-jetty ws-handler {:port 7778
                               :websockets true
                               :join? false}))

(defn start-browser-nrepl-server! []
  (log/info "Starting browser nREPL server on port 7777")
  (nrepl-server/start-server
    :port 7777
    :handler (nrepl-server/default-handler #'websocket-eval-middleware)))

(defn start! []
  (start-websocket-server!)
  (start-browser-nrepl-server!))

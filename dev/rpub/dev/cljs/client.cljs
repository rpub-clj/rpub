(ns rpub.dev.cljs.client
  (:require ["cherry-cljs/lib/compiler.js" :as cherry]
            ["preact/debug"]
            [cljs.pprint :as pprint]
            [rpub.lib.transit :as transit]))

(def cherry-state (atom nil))

(def cherry-opts
  #js{:repl true
      :async true
      :context "return"
      :elide-exports true})

(defn- eval-cherry-string [state-atom s]
  (let [st (cherry/compileStringEx s cherry-opts @cherry-state)
        code (str "(async function() {\n" (.-javascript st) "\n})()")
        ret (js/eval code)]
    (reset! state-atom st)
    ret))

(defn eval-cherry [x]
  (let [s (if (string? x) x (pr-str x))]
    (eval-cherry-string cherry-state s)))

(defn start-websocket-client!
  [{:keys [url on-open on-close on-error on-message]}]
  (let [ws (js/WebSocket. url)]
    (set! (.-onmessage ws) (fn [event] (on-message ws (.-data event))))
    (set! (.-onopen ws) (fn [] (when on-open (on-open ws))))
    (set! (.-onclose ws) (fn [] (when on-close (on-close ws))))
    (set! (.-onerror ws) (fn [error] (when on-error (on-error ws error))))))

(defn handle-ws-open []
  (println "[cljs] WebSocket opened"))

(defn handle-ws-close []
  (println "[cljs] WebSocket closed"))

(defn handle-ws-error [e]
  (js/console.log e))

(defn handle-ws-message [ws s]
  (let [{:keys [id code]} (transit/read s)
        on-eval (fn [value]
                  (let [pretty (with-out-str (pprint/pprint value))
                        msg {:id id, :value (pr-str value)}]
                    (println (str "[cljs] => " code "\n" pretty))
                    (.send ws (transit/write msg))))
        on-error (fn [err]
                   (on-eval #:error{:message (str err)
                                    :stack (.-stack err)})
                   (js/console.log err))]
    (doto (eval-cherry code)
      (.then on-eval on-error))))

(defn init-repl! []
  (println "[cljs] REPL started")
  (set! js/window.evalCherry eval-cherry)
  (start-websocket-client!
    {:url "ws://localhost:7778"
     :on-open handle-ws-open
     :on-close handle-ws-close
     :on-error handle-ws-error
     :on-message handle-ws-message}))

(def repl-init-commands
  '(do
     (ns rpub.dev.cljs.repl
       (:require [rpub.lib.tap :as tap]
                 [rpub.dev.cljs.client :as client]
                 [rpub.plugins.admin :as admin]
                 [rpub.lib.dag :as dag]
                 [rpub.dev.dag.viz :as dag-viz]
                 [rpub.dev.dag.viz.aliases :as dag-viz-aliases]))

     (defonce tap-fn (fn [x] (tap/remote-tap "/admin/api/tap" x)))
     (add-tap tap-fn)
     (client/init-repl!)

     (dag/enable-assertions!)

     (defn prepend-element [{:keys [page-id dag] :as _page-config}]
       [dag-viz/overlay
        {:storage-id page-id
         :aliases (get dag-viz-aliases/default-aliases page-id)
         :initial-nodes (dag-viz/initial-nodes dag)
         :initial-edges (dag-viz/initial-edges dag)}])

     (admin/start! {:prepend-element prepend-element
                    :tracing false})))

(defn start! []
  (eval-cherry repl-init-commands))

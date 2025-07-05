(ns rpub.main
  "The main entry point for using rPub as an application."
  (:require [babashka.cli :as cli]
            [clojure.set :as set]
            [clojure.string :as str]
            [nrepl.cmdline :as nrepl]
            [rpub.core :as rpub]
            [rpub.lib.logs :as logs]
            [rpub.lib.malli :as malli]
            [rpub.model.sqlite]
            [rpub.plugins.admin]
            [rpub.plugins.app]
            [taoensso.telemere :as tel]))

(def ^:private banner-text "
      ____        _
 _ __|  _ \\ _   _| |__
| '__| |_) | | | | '_ \\
| |  |  __/| |_| | |_) |
|_|  |_|    \\__,_|_.__/
")

(defn- print-banner []
  (doseq [line (rest (str/split-lines banner-text))]
    (tel/log! :info line))
  (tel/log! :info ""))

(def banner-defaults
  {:banner true})

(defn- print-memory []
  (let [bytes->mb #(Math/round (double (/ % 1024 1024)))
        runtime (Runtime/getRuntime)]
    (tel/log! :info
              (format (str "Initial Heap Size (Xms): %d MiB - "
                           "Max Heap Size (Xmx): %d MiB")
                      (bytes->mb (.totalMemory runtime))
                      (bytes->mb (.maxMemory runtime))))))

(defonce
  ^{:doc "An atom containing the current rPub server and REPL processes."}
  current-system
  (atom nil))

(def clj-repl-defaults
  "The default options for the Clojure REPL."
  {:clj-repl true
   :clj-repl-bind "0.0.0.0"
   :clj-repl-port 7888
   :clj-repl-flow-storm-middleware false})

(defn- flow-storm-middleware []
  (requiring-resolve 'flow-storm.nrepl.middleware/wrap-flow-storm))

(defn- start-clj-repl! [opts]
  (let [nrepl-cli-opts (-> (select-keys opts [:clj-repl-port :clj-repl-bind])
                           (set/rename-keys {:clj-repl-port :port
                                             :clj-repl-bind :bind}))
        nrepl-cli-opts' (cond-> nrepl-cli-opts
                          (:clj-repl-flow-storm-middleware opts)
                          (assoc :middleware [(flow-storm-middleware)]))
        repl-opts (nrepl/server-opts nrepl-cli-opts')
        repl-server (nrepl/start-server repl-opts)]
    (nrepl/ack-server repl-server repl-opts)
    (nrepl/save-port-file repl-server repl-opts)
    (tel/log! :info (nrepl/server-started-message repl-server repl-opts))))

(def malli-dev-defaults
  "The default options for Malli dev instrumentation."
  {:malli-dev false})

(def cljs-repl-defaults
  "The default options for the ClojureScript REPL."
  {:cljs-repl false})

(def logs-defaults
  {:logs-pretty false})

(defn start-cljs-repl! [_]
  ((requiring-resolve 'rpub.dev.cljs.server/start!)))

(defn setup-shutdown-hook! []
  (.addShutdownHook
    (Runtime/getRuntime)
    (Thread. (fn [] (tel/stop-handlers!)))))

(defn start!
  "Start the rPub server and an optional REPL.

  The REPL is enabled by default (see `rpub.main/repl-defaults`).

  Malli dev instrumentation is disabled by default
  (see `rpub.main/malli-dev-defaults`)."
  [& {:as opts}]
  (tel/uncaught->error!)
  (setup-shutdown-hook!)
  (let [opts' (merge (cli/parse-opts *command-line-args*) opts)
        logs-opts (merge logs-defaults opts')]
    (logs/setup! logs-opts)
    (let [banner-opts (merge banner-defaults opts')]
      (when (:banner banner-opts)
        (print-banner)))
    (print-memory)
    (let [clj-repl-opts (merge clj-repl-defaults opts')]
      (when (:clj-repl clj-repl-opts)
        (start-clj-repl! clj-repl-opts)))
    (let [malli-dev-opts (merge malli-dev-defaults opts')]
      (when (:malli-dev malli-dev-opts)
        (malli/start-dev! malli-dev-opts)))
    (let [cljs-repl-opts (merge cljs-repl-defaults opts')]
      (when (:cljs-repl cljs-repl-opts)
        (start-cljs-repl! cljs-repl-opts)))
    (reset! current-system {:app (rpub/start! opts')})))

(defn stop!
  "Stop the rPub server but not the REPL."
  []
  (when-let [s @current-system]
    (rpub/stop! s)
    (reset! current-system nil)))

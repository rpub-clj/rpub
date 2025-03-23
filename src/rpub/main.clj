(ns rpub.main
  "The main entry point for using rPub as an application."
  (:require [babashka.cli :as cli]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [nrepl.cmdline :as nrepl]
            [rpub.admin]
            [rpub.api]
            [rpub.app]
            [rpub.core :as rpub]
            [rpub.lib.malli :as malli]
            [rpub.model.sqlite]
            [rpub.plugins.content-types]))

(def ^:private banner-text "
      ____        _
 _ __|  _ \\ _   _| |__
| '__| |_) | | | | '_ \\
| |  |  __/| |_| | |_) |
|_|  |_|    \\__,_|_.__/
")

(defn- print-banner []
  (doseq [line (rest (str/split-lines banner-text))]
    (log/info line))
  (log/info ""))

(defn- print-memory []
  (let [bytes->mb #(Math/round (double (/ % 1024 1024)))
        runtime (Runtime/getRuntime)]
    (log/info
      (format (str "Initial Heap Size (Xms): %d MiB - "
                   "Max Heap Size (Xmx): %d MiB")
              (bytes->mb (.totalMemory runtime))
              (bytes->mb (.maxMemory runtime))))))

(defonce
  ^{:doc "An atom containing the current rPub server and REPL processes."}
  current-system
  (atom nil))

(def repl-defaults
  "The default options for the REPL."
  {:repl true
   :repl-bind "0.0.0.0"
   :repl-port 7888
   :repl-flow-storm-middleware false})

(defn- flow-storm-middleware []
  (requiring-resolve 'flow-storm.nrepl.middleware/wrap-flow-storm))

(defn- start-repl! [opts]
  (let [nrepl-cli-opts (-> (select-keys opts [:repl-port :repl-bind])
                           (set/rename-keys {:repl-port :port
                                             :repl-bind :bind}))
        nrepl-cli-opts' (cond-> nrepl-cli-opts
                          (:repl-flow-storm-middleware opts)
                          (assoc :middleware [(flow-storm-middleware)]))
        repl-opts (nrepl/server-opts nrepl-cli-opts')
        repl-server (nrepl/start-server repl-opts)]
    (nrepl/ack-server repl-server repl-opts)
    (nrepl/save-port-file repl-server repl-opts)
    (log/info (nrepl/server-started-message repl-server repl-opts))))

(def malli-dev-defaults
  "The default options for Malli dev instrumentation."
  {:malli-dev false})

(def cljs-repl-defaults
  "The default options for the CLJS REPL."
  {:cljs-repl false})

(defn start-cljs-repl! [_]
  ((requiring-resolve 'rpub.dev.cljs.server/start!)))

(defn start!
  "Start the rPub server and an optional REPL.

  The REPL is enabled by default (see `rpub.main/repl-defaults`).

  Malli dev instrumentation is disabled by default
  (see `rpub.main/malli-dev-defaults`)."
  [& {:as opts}]
  (let [opts' (merge (cli/parse-opts *command-line-args*) opts)]
    (print-banner)
    (print-memory)
    (let [repl-opts (merge repl-defaults opts')]
      (when (:repl repl-opts)
        (start-repl! repl-opts)))
    (let [malli-dev-opts (merge malli-dev-defaults opts')]
      (when (:malli-dev malli-dev-opts)
        (malli/start-dev! malli-dev-opts)))
    (let [cljs-repl-opts (merge cljs-repl-defaults opts')]
      (when (:cljs-repl cljs-repl-opts)
        (start-cljs-repl! cljs-repl-opts)))
    (reset! current-system (rpub/start! opts'))))

(defn stop!
  "Stop the rPub server but not the REPL."
  []
  (when-let [s @current-system]
    (rpub/stop! s)
    (reset! current-system nil)))

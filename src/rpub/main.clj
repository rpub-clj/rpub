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

(defn- start-repl! [opts]
  (let [nrepl-cli-opts (-> (select-keys opts [:repl-port :repl-bind])
                           (set/rename-keys {:repl-port :port
                                             :repl-bind :bind}))
        repl-opts (nrepl/server-opts nrepl-cli-opts)
        repl-server (nrepl/start-server repl-opts)]
    (nrepl/ack-server repl-server repl-opts)
    (nrepl/save-port-file repl-server repl-opts)
    (log/info (nrepl/server-started-message repl-server repl-opts))))

(def repl-defaults
  "The default options for the REPL."
  {:repl true
   :repl-bind "0.0.0.0"
   :repl-port 7888})

(defn start!
  "Start the rPub server and an optional REPL.

  The REPL is enabled by default (see `rpub.main/repl-defaults`)."
  [& {:as opts}]
  (let [opts' (merge (cli/parse-opts *command-line-args*) opts)]
    (print-banner)
    (print-memory)
    (let [repl-opts (merge repl-defaults opts')]
      (when (:repl repl-opts)
        (start-repl! repl-opts)))
    (reset! current-system (rpub/start! opts'))))

(defn stop!
  "Stop the rPub server but not the REPL."
  []
  (when-let [s @current-system]
    (rpub/stop! s)
    (reset! current-system nil)))

(ns rpub.tasks
  "Tasks for rPub projects (compatible with Babashka)."
  (:refer-clojure :exclude [test])
  (:require [babashka.cli :as cli]
            [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(alter-var-root (var babashka.process/*defaults*) assoc
                :pre-start-fn
                (fn [opts]
                  (apply println "+" (:cmd opts))))

(def ^:no-doc default-opts
  {:data-dir "data"})

(defn ^:no-doc init-data
  [{:keys [data-dir]
    mvn-version :mvn/version}]
  (fs/create-dirs data-dir)
  (let [data-app (fs/file data-dir "app.clj")
        data-deps (fs/file data-dir "deps.edn")]
    (when-not (fs/exists? data-deps)
      (let [c (-> (slurp (io/resource "data/deps.edn"))
                  (str/replace "<mvn/version>" mvn-version))]
        (spit data-deps c)))
    (when-not (fs/exists? data-app)
      (spit data-app (slurp (io/resource "data/app.clj"))))))

(defn supervisor
  "Starts a supervisor that auto-restarts an rPub server.

  The supervisor starts a child rPub server using the `clojure` CLI command and
  restarts it automatically if it stops with exit code 0. This allows rPub to
  load new Clojure dependencies after modifying the `deps.edn` file through the
  admin UI.

  This function is meant to be run using Babashka to minimize memory overhead."
  [& {:as opts}]
  (let [opts' (merge default-opts (cli/parse-opts *command-line-args*) opts)
        {:keys [data-dir]} opts']
    (init-data opts')
    (loop []
      (let [deps {:deps {'dev.rpub/app {:local/root data-dir}}}
            app (apply p/shell
                       "clojure"
                       "-Sdeps" (pr-str deps)
                       "-M" "-m" "app"
                       *command-line-args*)]
        (when (zero? (:exit app))
          (recur))))))

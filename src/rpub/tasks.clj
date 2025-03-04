(ns rpub.tasks
  "Tasks for rPub projects (compatible with Babashka)."
  (:refer-clojure :exclude [test])
  (:require [babashka.cli :as cli]
            [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(alter-var-root (var babashka.process/*defaults*) assoc
                :pre-start-fn
                (fn [opts]
                  (apply println "+" (:cmd opts))))

(defn- init-data
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

(def ^:private default-opts
  {:data-dir "data"})

(defn- init [& _]
  (init-data default-opts))

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

(defn ^:no-doc dev [& _]
  (let [opts (merge default-opts (cli/parse-opts *command-line-args*))]
    (init-data opts)
    (loop []
      (let [app (apply p/shell
                       "clojure -M:user:common:dev:test:app"
                       "--reload"
                       "--no-content-security-policy"
                       "--no-error-page"
                       "--repl-port" "0"
                       *command-line-args*)]
        (when (zero? (:exit app))
          (recur))))))

(defn ^:no-doc prod-admin-css []
  (p/shell
    {:extra-env {:NODE_ENV "production"}}
    "./node_modules/.bin/tailwindcss"
    "--minify"
    "--postcss" "resources/css/admin/postcss.config.js"
    "--config" "resources/css/admin/tailwind.config.js"
    "--input" "resources/css/admin/tailwind.css"
    "--output" "resources/public/css/admin/main.css"))

(defn ^:no-doc dev-admin-css []
  (p/shell
    {:extra-env {:NODE_ENV "development"}}
    "./node_modules/.bin/tailwindcss"
    "--watch"
    "--postcss" "resources/css/admin/postcss.config.js"
    "--config" "resources/css/admin/tailwind.config.js"
    "--input" "resources/css/admin/tailwind.css"
    "--output" "target/public/css/admin/main.css"))

(defn ^:no-doc dev-cljs []
  (p/shell
    {:extra-env {:NODE_ENV "development"}}
    "./node_modules/.bin/cherry" "run" "build.cljs" "--watch"))

(defn ^:no-doc prod-cljs []
  (p/shell
    {:extra-env {:NODE_ENV "production"}}
    "./node_modules/.bin/cherry" "run" "build.cljs"))

(defn ^:no-doc flowstorm
  [& {:keys [port debugger-host]
      :or {port 7888
           debugger-host "host.docker.internal"}}]
  (let [deps '{:deps {com.github.flow-storm/flow-storm-dbg
                      {:mvn/version "RELEASE"}}}]
    (p/shell
      "clojure"
      "-Sforce"
      "-Sdeps" (pr-str deps)
      "-X" (pr-str 'flow-storm.debugger.main/start-debugger)
      ":port" (pr-str port)
      ":debugger-host" (pr-str debugger-host))))

(def ^:private deps-edn (delay (edn/read-string (slurp "deps.edn"))))
(def ^:private version (delay (get-in @deps-edn [:aliases :neil :project :version])))

(defn ^:no-doc docker-build
  [{:keys [platform]
    :or {platform "linux/amd64,linux/arm64"}}]
  (p/shell "docker build"
           "-t" (format "rpub/rpub:%s" @version)
           "--platform" platform
           "."))

(defn ^:no-doc docker-push [_]
  (p/shell (format "docker push rpub/rpub:%s" @version)))

(defn ^:no-doc test [_]
  (apply p/shell "clojure -M:common:default-plugins:dev:test" *command-line-args*))

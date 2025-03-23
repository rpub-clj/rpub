(ns rpub.dev.tasks
  {:no-doc true}
  (:refer-clojure :exclude [test])
  (:require [babashka.cli :as cli]
            [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [rpub.tasks :as tasks]))

(defn dev [& {:as opts}]
  (let [opts' (merge tasks/default-opts
                     (cli/parse-opts *command-line-args*)
                     opts)]
    (tasks/init-data opts')
    (loop []
      (let [app (apply p/shell
                       "clojure -M:user:common:dev:test:app"
                       "--no-content-security-policy"
                       "--no-error-page"
                       "--reload"
                       "--malli-dev"
                       "--clj-repl-port" "0"
                       "--clj-repl-flow-storm-middleware"
                       "--cljs-repl"
                       *command-line-args*)]
        (when (zero? (:exit app))
          (recur))))))

(def ^:private deps-edn (delay (edn/read-string (slurp "deps.edn"))))

(defn lint [& _]
  (p/shell "mkdir -p .clj-kondo")
  (let [classpath (:out (p/shell {:out :string} "clojure -Spath"))
        paths (concat (-> (set (get-in @deps-edn [:aliases :dev :replace-paths]))
                          (disj "target"))
                      (fs/glob "." "*.{clj,cljs,cljc}"))
        _ (p/shell "clj-kondo"
                   "--lint" classpath
                   "--dependencies"
                   "--copy-configs"
                   "--skip-lint")
        proc (apply p/shell
                    {:continue true}
                    "clj-kondo"
                    (mapcat (fn [p] ["--lint" (str p)]) paths))]
    (System/exit (:exit proc))))

(defn prod-admin-css []
  (p/shell "rm -rf target/public/css")
  (p/shell
    {:extra-env {:NODE_ENV "production"}}
    "./node_modules/.bin/tailwindcss"
    "--minify"
    "--postcss" "resources/css/admin/postcss.config.js"
    "--config" "resources/css/admin/tailwind.config.js"
    "--input" "resources/css/admin/tailwind.css"
    "--output" "target/public/css/admin/main.css"))

(defn dev-admin-css []
  (p/shell
    {:extra-env {:NODE_ENV "development"}}
    "./node_modules/.bin/tailwindcss"
    "--watch"
    "--postcss" "resources/css/admin/postcss.config.js"
    "--config" "resources/css/admin/tailwind.config.js"
    "--input" "resources/css/admin/tailwind.css"
    "--output" "target/public/css/admin/main.css"))

(defn copy-css []
  (p/shell "rm -rf resources/public/css")
  (p/shell "cp -r target/public/css resources/public/css"))

(defn dev-cljs []
  (p/shell "rm -rf target/public/js")
  (apply p/shell
         "./node_modules/.bin/cherry" "run" "build.cljs"
         "--watch" "--no-minify"
         *command-line-args*))

(defn prod-cljs []
  (p/shell "rm -rf target/public/js")
  (apply p/shell
         "./node_modules/.bin/cherry" "run" "build.cljs"
         *command-line-args*))

(defn copy-js []
  (p/shell "rm -rf resources/public/js")
  (p/shell "cp -r target/public/js resources/public/js"))

(defn flowstorm
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

(def version (delay (get-in @deps-edn [:aliases :neil :project :version])))

(defn docker-dockerfile [_]
  (let [s (-> (slurp "Dockerfile.template")
              (str/replace "RPUB_MVN_VERSION" @version))]
    (spit "Dockerfile" s)))

(defn docker-compose-config [_]
  (let [s (-> (slurp "docker-compose.template.yaml")
              (str/replace "RPUB_MVN_VERSION" @version))]
    (spit "docker-compose.yaml" s)))

(defn docker-build
  [{:keys [platform]
    :or {platform "linux/amd64,linux/arm64"}}]
  (p/shell "docker build"
           "-t" (format "rpub/rpub:%s" @version)
           "--platform" platform
           "."))

(defn docker-push [_]
  (p/shell (format "docker push rpub/rpub:%s" @version)))

(defn test [_]
  (apply p/shell "clojure -M:common:default-plugins:dev:test" *command-line-args*))

(defn install [_]
  (p/shell "clojure -X:dev:deps-deploy install"))

(defn deploy [_]
  (p/shell "clojure -X:dev:deps-deploy deploy"))

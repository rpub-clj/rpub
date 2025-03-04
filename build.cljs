(ns build
  (:require ["esbuild" :as esbuild]
            ["fast-glob$default" :as fast-glob]
            ["node:child_process" :as cp]
            ["node:fs" :as fs]
            ["node:path" :as path]
            [clojure.string :as str]))

(defn write-manifest [result manifest-path]
  (let [result' (js->clj result)
        manifest (->> (keys (get-in result' ["metafile" "outputs"]))
                      (map #(str/replace % #"^resources/public/js/" ""))
                      (map (fn [s] [(str/replace s #"^(.+)\.[A-Z0-9]{8}\.js$" "$1.js") s]))
                      (into {}))
        json (js/JSON.stringify (clj->js manifest) nil 2)]
    (fs/writeFileSync manifest-path json)))

(def manifest-path "resources/public/js/manifest.json")

(def manifest-plugin
  {:name "manifest"
   :setup (fn [build]
            (set! (-> build .-initialOptions .-metafile) true)
            (.onEnd build #(write-manifest % manifest-path)))})

(defn ^:async compile-cherry []
  (let [cmd (concat ["./node_modules/.bin/cherry" "compile"]
                    (js/await (fast-glob "src/**/*.{cljc,cljs}"))
                    ["--output-dir=target/cherry"])]
    (js/Promise.
      (fn [resolve reject]
        (.exec cp (str/join " " cmd)
               (fn [error stdout stderr]
                 (if error
                   (reject error)
                   (do
                     (when stderr (js/console.error stderr))
                     (when stdout (js/console.log stdout))
                     (resolve)))))))))

(def cherry-loader
  {:name "cherry-loader"
   :setup (fn [build] (.onStart build compile-cherry))})

(def ctx
  (js/await
    (.context
      esbuild
      (clj->js
        {:entryPoints ["target/cherry/src/**/*.mjs",
                       "target/cherry/src/**/*.jsx"]
         :entryNames "[dir]/[name].[hash]"
         :format "esm"
         :outdir "resources/public/js/rpub"
         :minify true
         :jsx "automatic"
         :plugins [cherry-loader manifest-plugin]}))))

(if (contains? (set js/process.argv) "--watch")
  (do
    (js/await (.watch ctx))
    (println "Watching..."))
  (do
    (js/await (.rebuild ctx))
    (println "Building...")
    (js/process.exit 0)))

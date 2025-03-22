(ns build
  (:require ["cherry-cljs/lib/compiler.js" :as cherry]
            ["esbuild" :as esbuild]
            ["node:fs" :as fs]
            ["node:path" :as path]
            ["node:process" :as process]
            ["node:url" :as url]
            [clojure.string :as str]))

(def __filename (url/fileURLToPath js/import.meta.url))
(def __dirname (path/dirname __filename))

(defn write-manifest [result manifest-path]
  (let [result' (js->clj result)
        manifest (->> (keys (get-in result' ["metafile" "outputs"]))
                      (map #(str/replace % #"^target/public/js/" ""))
                      (map (fn [s] [(str/replace s #"^(.+)\.[A-Z0-9]{8}\.js$" "$1.js") s]))
                      (into {}))
        json (js/JSON.stringify (clj->js manifest) nil 2)]
    (fs/writeFileSync manifest-path json)))

(def manifest-path "target/public/js/manifest.json")

(def manifest-plugin
  {:name "manifest"
   :setup (fn [build]
            (set! (-> build .-initialOptions .-metafile) true)
            (.onEnd build #(write-manifest % manifest-path)))})

(defn ^:async compile-cherry [args]
  (println "[esbuild] cherry compile" (path/relative __dirname (.-path args)))
  (let [contents (js/await (.readFile (.-promises fs) (.-path args) "utf8"))
        contents' (cherry/compileString contents)]
    #js{:contents contents', :loader "jsx"}))

(def cherry-loader
  {:name "cherry-loader"
   :setup (fn [build]
            (.onLoad
              build
              #js{:filter #"\.clj(s|c)$"}
              compile-cherry))})

(def ctx
  (js/await
    (.context
      esbuild
      (clj->js
        {:entryPoints (for [dir ["src" "dev"]
                            ext ["cljs" "cljc"]]
                        (str dir "/**/*." ext))
         :entryNames "[dir]/[name].[hash]"
         :format "esm"
         :outdir "target/public/js/rpub"
         :minify (not (contains? (set process/argv) "--no-minify"))
         :jsx "automatic"
         :plugins [cherry-loader manifest-plugin]}))))

(if (contains? (set process/argv) "--watch")
  (do
    (js/await (.watch ctx))
    (println "Watching..."))
  (do
    (js/await (.rebuild ctx))
    (println "Building...")
    (process/exit 0)))

(ns build
  (:require [clojure.edn :as edn]
            [clojure.tools.build.api :as b]))

(def deps-edn (edn/read-string (slurp "deps.edn")))
(def lib (get-in deps-edn [:aliases :neil :project :name]))
(def version (get-in deps-edn [:aliases :neil :project :version]))
(def class-dir "target/classes")
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(def basis
  (delay
    (b/create-basis
      {:project "deps.edn"
       #_#_:aliases [:flowstorm]})))

(defn clean [_]
  (b/delete {:path "target"})
  (b/delete {:path "resources/public/css"})
  (b/delete {:path "resources/public/js"}))

(defn- compile-css [_]
  (b/process {:command-args ["bb" "prod:css"]})
  (b/process {:command-args ["bb" "copy-css"]}))

(defn- compile-cljs [_]
  (b/process {:command-args ["bb" "prod:cljs"]})
  (b/process {:command-args ["bb" "copy-js"]}))

(def pom-data
  [[:licenses
    [:license
     [:name "MIT License"]
     [:url "https://opensource.org/license/mit"]]]
   [:developers
    [:developer
     [:name "Radford Smith"]]]])

(defn jar [_]
  (clean nil)
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis @basis
                :src-dirs ["src"]
                :pom-data pom-data})
  (compile-css nil)
  (compile-cljs nil)
  (b/copy-dir {:src-dirs ["target/public"]
               :target-dir (str class-dir "/public")})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

#_(defn uber [_]
    (clean nil)
    (b/copy-dir {:src-dirs ["src" "resources"]
                 :target-dir class-dir})
    (compile-css nil)
    (compile-cljs nil)
    (b/copy-dir {:src-dirs ["target/public"]
                 :target-dir (str class-dir "/public")})
    (b/compile-clj {:basis @basis
                    :ns-compile '[rpub.main]
                    :class-dir class-dir})
    (b/uber {:class-dir class-dir
             :uber-file uber-file
             :basis @basis
             :main 'rpub.main}))

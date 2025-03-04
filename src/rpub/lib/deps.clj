(ns rpub.lib.deps
  {:no-doc true}
  (:require [babashka.fs :as fs]
            [babashka.http-client :as http]
            [babashka.json :as json]
            [borkdude.rewrite-edn :as r]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [rewrite-clj.node :as n]
            [rewrite-clj.zip :as z]
            [rpub.lib.git :as git]
            [version-clj.core :as version-clj])
  (:import (java.io File)))

(defn- repo-plugin-suffix [plugin]
  (let [s (str (:key plugin))
        [_ ns-suffix] (re-matches #":rpub\.plugins\.(.+)/(.+)" s)]
    ns-suffix))

(defn ->plugin-lib-sym [plugin]
  (let [plugin-name (repo-plugin-suffix plugin)]
    (symbol "dev.rpub" (str "plugins." plugin-name))))

(defn- http-get-json [uri]
  (let [response (http/get uri {:headers {"Accept" "application/json"}})]
    (json/read-str (:body response))))

(defn- get-clojars-artifact [qlib]
  (http-get-json
    (format "https://clojars.org/api/artifacts/%s"
            qlib)))

(defn- stable-version? [version-str]
  (empty? (set/intersection (:qualifiers (version-clj/parse version-str))
                            #{"rc" "alpha" "beta" "snapshot" "milestone"})))

(defn- first-stable-version [versions]
  (->> versions
       (filter stable-version?)
       first))

(defn- clojars-versions [qlib {:keys [limit] :or {limit 10}}]
  (let [body (get-clojars-artifact qlib)]
    (->> body
         :recent_versions
         (map :version)
         (take limit))))

(defn- latest-stable-clojars-version
  [qlib]
  (first-stable-version (clojars-versions qlib {:limit 100})))

(defn- latest-clojars-version [qlib]
  (first (clojars-versions qlib {:limit 100})))

(defn- search-mvn [qlib limit]
  (:response
    (http-get-json
      (format "https://search.maven.org/solrsearch/select?q=g:%s+AND+a:%s&rows=%s&core=gav&wt=json"
              (namespace qlib)
              (name qlib)
              (str limit)))))

(defn- mvn-versions [qlib {:keys [limit] :or {limit 10}}]
  (let [payload (search-mvn qlib limit)]
    (->> payload
         :docs
         (map :v))))

(defn- latest-stable-mvn-version [qlib]
  (first-stable-version (mvn-versions qlib {:limit 100})))

(defn- latest-mvn-version [qlib]
  (first (mvn-versions qlib {:limit 100})))

(defn- edn-string [opts] (slurp (:deps-file opts)))

(defn- ->edn-nodes [edn-string] (r/parse-string edn-string))

(defn dep-add [{:keys [opts]}]
  (let [edn-string (edn-string opts)
        edn-nodes (->edn-nodes edn-string)
        lib (:lib opts)
        lib (symbol lib)
        lib (symbol (or (namespace lib) (name lib))
                    (name lib))
        alias (:alias opts)
        explicit-git-sha? (or (:sha opts) (:latest-sha opts))
        explicit-git-tag? (or (:tag opts) (:latest-tag opts))
        [version coord-type?]
        (cond explicit-git-tag?
              [(or (and (:tag opts)
                        (git/find-github-tag lib (:tag opts)))
                   (git/latest-github-tag lib)) :git/tag]
              explicit-git-sha?
              [(or (:sha opts) (git/latest-github-sha lib)) :git/sha]
              :else
              (or
                (when-let [v (:version opts)]
                  [v :mvn])
                (when-let [v (latest-stable-clojars-version lib)]
                  [v :mvn])
                (when-let [v (latest-stable-mvn-version lib)]
                  [v :mvn])
                (when-let [v (git/latest-github-sha lib)]
                  [v :git/sha])
                (when-let [v (latest-clojars-version lib)]
                  [v :mvn])
                (when-let [v (latest-mvn-version lib)]
                  [v :mvn])))
        _ (when-not version
            (throw (ex-info (str "Couldn't find version for lib: " lib) {:babashka/exit 1})))
        missing? (nil? version)
        mvn? (= :mvn coord-type?)
        git-sha? (= :git/sha coord-type?)
        git-tag? (= :git/tag coord-type?)
        git-url (when (or git-sha? git-tag?)
                  (or (:git/url opts)
                      (str "https://github.com/" (git/clean-github-lib lib))))
        as (or (:as opts) lib)
        existing-aliases (-> edn-string edn/read-string :aliases)
        path (if alias
               [:aliases
                alias
                (if (get-in existing-aliases [alias :deps]) :deps :extra-deps)
                as]
               [:deps as])
        nl-path (if (and alias
                         (not (contains? existing-aliases alias)))
                  [:aliases alias]
                  path)
        edn-nodes (if (r/get-in edn-nodes nl-path)
                    ;; if this dep already exists, don't touch it.
                    ;; We risk loosing :exclusions and other properties.
                    edn-nodes
                    ;; otherwise, force newlines!
                    ;; force newline in
                    ;;
                    ;;     [:deps as] if no alias
                    ;;     [:aliases alias] if alias DNE
                    ;;     [:aliases alias :deps as] if :deps present
                    ;;     [:aliases alias :extra-deps as] if alias exists
                    (-> edn-nodes (r/assoc-in nl-path nil) str r/parse-string))
        nodes (cond
                missing? edn-nodes
                mvn?
                (r/assoc-in edn-nodes (conj path :mvn/version) version)
                git-sha?
                ;; multiple steps to force newlines
                (cond-> edn-nodes
                  (not (:omit-git-url opts)) (r/assoc-in (conj path :git/url)
                                                         git-url)
                  true str
                  true r/parse-string
                  true (r/assoc-in (conj path :git/sha) version)
                  true (r/update-in path r/dissoc :sha))

                git-tag?
                ;; multiple steps to force newlines
                (-> edn-nodes
                    (r/assoc-in (conj path :git/url) git-url)
                    str
                    r/parse-string
                    (r/assoc-in (conj path :git/tag) (-> version :name))
                    str
                    r/parse-string
                    (r/assoc-in (conj path :git/sha)
                                (some-> version :commit :sha (subs 0 7)))))
        nodes (if-let [root (and (or git-sha? git-tag?) (:deps/root opts))]
                (-> nodes
                    (r/assoc-in (conj path :deps/root) root))
                nodes)
        s (str (str/trim (str nodes)) "\n")]
    (when-not missing?
      (spit (:deps-file opts) s))))

(defn deps-path []
  (if (fs/exists? "data/deps.edn")
    "data/deps.edn"
    "deps.edn"))

(defn add-deps [plugin]
  (let [plugin-name (repo-plugin-suffix plugin)
        neil-opts {:deps-file (deps-path)
                   :lib (->plugin-lib-sym plugin)
                   :git/url "https://github.com/rpub-clj/plugins.git"
                   :sha (:sha plugin)
                   :deps/root (str "plugins/" plugin-name)}]
    (log/info (dep-add {:opts neil-opts}))))

(defn- find-main-file [dir]
  (let [depth #(count (str/split (.getPath %) (re-pattern File/separator)))]
    (->> (file-seq (io/file dir))
         (filter #(and (.isFile %) (.endsWith (.getName %) ".clj")))
         (sort-by depth)
         first)))

(defn ->plugin-ns [plugin]
  (let [plugin-name (repo-plugin-suffix plugin)]
    (symbol (str "rpub.plugins." plugin-name))))

(defn add-require [plugin]
  (let [app-file (io/file "data/app.clj")
        main-file (or (and (.exists app-file) app-file)
                      (find-main-file "src"))
        zipper (z/of-file main-file)
        new-deps (-> zipper
                     (z/find-value z/next 'ns)
                     (z/find-value z/next :require)
                     z/rightmost
                     (z/insert-right [(->plugin-ns plugin)])
                     (z/insert-space-right 11)
                     (z/insert-newline-right))]
    (spit main-file (z/root-string new-deps))))

(defn remove-require [plugin]
  (let [app-file (io/file "data/app.clj")
        main-file (or (and (.exists app-file) app-file)
                      (find-main-file "src"))
        zipper (z/of-file main-file)
        requires (-> zipper
                     (z/find-value z/next 'ns)
                     (z/find-value z/next :require)
                     z/up)
        match? (fn [form]
                 (or (and (symbol? form) (= form (->plugin-ns plugin)))
                     (and (vector? form) (= (first form) (->plugin-ns plugin)))))
        updated (->> (z/sexpr requires) (remove match?) n/coerce)
        new-deps (z/replace requires updated)]
    (spit main-file (z/root-string new-deps))))

(defn- edn-nodes [edn-string]
  (r/parse-string edn-string))

(defn remove-deps [plugin]
  (let [deps-file (deps-path)
        root (edn-nodes (slurp deps-file))
        root' (r/update root :deps #(r/dissoc % (->plugin-lib-sym plugin)))]
    (spit deps-file (str root'))))

(comment
  (remove-require {:key :rpub.plugins.seo})
  (remove-deps nil)
  (System/exit 0))

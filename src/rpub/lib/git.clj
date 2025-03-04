(ns rpub.lib.git
  {:no-doc true}
  (:require [babashka.http-client :as http]
            [babashka.json :as json]
            [babashka.process :refer [sh]]
            [clojure.string :as str]))

(defn- http-get-json [uri]
  (let [response (http/get uri {:headers {"Accept" "application/json"}})]
    (json/read-str (:body response))))

(defn default-branch [lib]
  (get (http-get-json (format "https://api.github.com/repos/%s/%s"
                              (namespace lib) (name lib)))
       :default_branch))

(defn clean-github-lib [lib]
  (let [lib (str/replace lib "com.github." "")
        lib (str/replace lib "io.github." "")
        lib (symbol lib)]
    lib))

(defn latest-github-sha [lib]
  (let [lib (clean-github-lib lib)
        branch (default-branch lib)]
    (get (http-get-json (format "https://api.github.com/repos/%s/%s/commits/%s"
                                (namespace lib) (name lib) branch))
         :sha)))

(defn list-github-tags [lib]
  (let [lib (clean-github-lib lib)]
    (http-get-json (format "https://api.github.com/repos/%s/%s/tags"
                           (namespace lib) (name lib)))))

(defn latest-github-tag [lib]
  (-> (list-github-tags lib)
      first))

(defn find-github-tag [lib tag]
  (->> (list-github-tags lib)
       (filter #(= (:name %) tag))
       first))

(defn parse-status [line]
  (let [[[_ status path]] (re-seq #"^(.{2}) (.+)$" line)]
    {:status status :path path}))

(defn status [git-opts]
  (let [cmd "git status --porcelain=v1"
        {:keys [out err]} (sh cmd git-opts)]
    (if-not (str/blank? err)
      {:err err}
      {:statuses (if (str/blank? out)
                   '()
                   (map parse-status (str/split-lines out)))})))

(defn tag [message git-opts]
  (let [cmd ["git" "tag" "-a" message "-m" message]
        {:keys [err]} (sh cmd git-opts)]
    (when (seq err) (throw (ex-info err {})))))

(defn add [files git-opts]
  (sh (concat ["git" "add"] files) git-opts))

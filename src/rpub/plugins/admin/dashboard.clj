(ns rpub.plugins.admin.dashboard
  {:no-doc true}
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [rpub.lib.html :as html]
            [rpub.model :as model]
            [rpub.model.app :as-alias model-app]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.content-types :as content-types]))

(defn- get-version-from-deps-edn []
  (let [deps-file (fs/file "deps.edn")]
    (when (fs/exists? deps-file)
      (let [deps-edn (edn/read-string (slurp deps-file))]
        (get-in deps-edn [:aliases :neil :project :version])))))

(defn- get-version-from-pom []
  (when-let [pom (io/resource "META-INF/maven/dev.rpub/rpub/pom.xml")]
    (let [lines (str/split-lines (slurp pom))
          [_ version] (some #(re-matches #" +<version>(.+)</version>" %) lines)]
      version)))

(def ^:private rpub-version
  (delay
    (or (get-version-from-deps-edn)
        (get-version-from-pom))))

(defn- dashboard-handler [{:keys [current-user plugins settings] :as req}]
  (let [content-types (content-types/get-content-types
                        (::content-types/model req)
                        {:count-items true})
        theme (-> (model/active-theme req) (select-keys [:label]))
        activated-plugins' (->> plugins
                                (filter #(and (model/plugin-visible? %) (:activated %)))
                                (map #(select-keys % [:label])))
        current-user' (select-keys current-user [:id :username])
        settings' (-> (select-keys settings [:permalink-single])
                      (update-vals #(select-keys % [:id :label :value])))]
    (helpers/page-response
      req
      {:title "Dashboard"
       :primary
       (html/custom-element
         [:dashboard-page
          {:rpub-version @rpub-version
           :content-types content-types
           :theme theme
           :activated-plugins activated-plugins'
           :current-user current-user'
           :settings settings'}])})))

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin" {:get #'dashboard-handler}]]])

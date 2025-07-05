(ns rpub.plugins.admin.themes
  {:no-doc true}
  (:require [ring.util.response :as response]
            [rpub.lib.html :as html]
            [rpub.model :as model]
            [rpub.model.app :as-alias model-app]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- all-themes-handler [{:keys [model themes current-user] :as req}]
  (let [[theme-name-setting] (model/get-settings model {:keys [:theme-name]})
        custom-themes (->> (model/get-themes model {})
                           (map #(assoc % :editable true)))
        themes' (->> (concat themes custom-themes)
                     (map #(select-keys % [:id :label :description :editable]))
                     (sort-by :label))]
    (helpers/page-response
      req
      {:title "Themes"
       :primary
       (html/custom-element
         [:all-themes-page {:theme-name-setting theme-name-setting
                            :themes themes'
                            :current-user current-user}])})))

(defn- new-theme-handler
  [{:keys [current-user] :as req}]
  (let [theme (model/->theme {:new true})]
    (helpers/page-response
      req
      {:title "New Theme"
       :primary
       (html/custom-element
         [:single-theme-page {:theme theme
                              :current-user current-user}])})))

(defn- edit-theme-handler
  [{:keys [path-params model current-user] :as req}]
  (let [{:keys [theme-id]} path-params
        [theme] (model/get-themes model {:ids [theme-id]})
        theme' (select-keys theme [:id :label :value])]
    (helpers/page-response
      req
      {:title (format "Theme: %s" (:label theme))
       :primary
       (html/custom-element
         [:single-theme-page {:theme theme'
                              :current-user current-user}])})))

(defn create-theme-handler
  [{:keys [body-params model current-user] :as _req}]
  (let [theme (-> (select-keys body-params [:id :label :value])
                  (assoc :current-user current-user)
                  model/->theme)]
    (model/update-theme! model theme)
    (response/response {:success true})))

(defn update-theme-handler
  [{:keys [body-params model current-user] :as _req}]
  (let [theme (-> (select-keys body-params [:id :label :value])
                  (assoc :current-user current-user)
                  model/->theme)]
    (model/update-theme! model theme)
    (response/response {:success true})))

(defn- single-theme-handler [{:keys [path-params] :as req}]
  (if (= (:theme-id path-params) "new")
    (new-theme-handler req)
    (edit-theme-handler req)))

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin/api/create-theme" {:post #'create-theme-handler}]
    ["/admin/api/update-theme" {:post #'update-theme-handler}]
    ["/admin/themes" {:get #'all-themes-handler}]
    ["/admin/themes/{theme-id}" {:get #'single-theme-handler}]]])

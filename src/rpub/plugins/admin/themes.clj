(ns rpub.plugins.admin.themes
  {:no-doc true}
  (:require [ring.util.response :as response]
            [rpub.lib.html :as html]
            [rpub.model.settings :as settings]
            [rpub.model.themes :as themes]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- all-themes-handler [{:keys [model themes current-user] :as req}]
  (let [[theme-name-setting] (settings/get-settings model {:keys [:theme-name]})
        custom-themes (->> (themes/get-themes model {})
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
  (let [theme (themes/->theme {:new true})]
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
        [theme] (themes/get-themes model {:ids [theme-id]})
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
  (let [theme (-> (:theme body-params)
                  (select-keys [:id :label :value])
                  (assoc :current-user current-user)
                  themes/->theme)]
    (themes/create-theme! model theme)
    (response/response (select-keys theme [:id]))))

(defn update-theme-handler
  [{:keys [body-params model current-user] :as _req}]
  (let [theme (-> (:theme body-params)
                  (select-keys [:id :label :value])
                  (assoc :current-user current-user)
                  themes/->theme)]
    (themes/update-theme! model theme)
    (response/response {:success true})))

(defn delete-theme-handler [{:keys [body-params model] :as _req}]
  (let [[theme] (themes/get-themes model {:ids [(:id body-params)]})]
    (if-not theme
      (response/bad-request {:success false})
      (do
        (themes/delete-theme! model theme)
        (response/response {:success true})))))

(defn- single-theme-handler [{:keys [path-params] :as req}]
  (if (= (:theme-id path-params) "new")
    (new-theme-handler req)
    (edit-theme-handler req)))

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin/api/create-theme" {:post #'create-theme-handler}]
    ["/admin/api/update-theme" {:post #'update-theme-handler}]
    ["/admin/api/delete-theme" {:post #'delete-theme-handler}]
    ["/admin/themes" {:get #'all-themes-handler}]
    ["/admin/themes/{theme-id}" {:get #'single-theme-handler}]]])

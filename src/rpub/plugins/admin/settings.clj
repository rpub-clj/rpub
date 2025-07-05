(ns rpub.plugins.admin.settings
  {:no-doc true}
  (:require [medley.core :as medley]
            [ring.util.response :as response]
            [rpub.lib.html :as html]
            [rpub.model :as model]
            [rpub.model.app :as-alias model-app]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- settings-handler [{:keys [model] :as req}]
  (helpers/page-response
    req
    {:title "Settings"
     :primary
     (let [settings (model/get-settings
                      model
                      {:keys [:site-title
                              :site-base-url
                              :site-description
                              :site-subtitle
                              :contact-email
                              :footer-links]})]
       (html/custom-element
         [:settings-page {:settings settings}]))}))

(defn- update-settings-handler
  [{:keys [model current-user body-params] :as _req}]
  (let [updated-setting-index (->> (get body-params :settings)
                                   (map #(select-keys % [:key :value]))
                                   (medley/index-by :key))
        ks (keys updated-setting-index)
        existing-setting-index (->> (model/get-settings model {:keys ks})
                                    (medley/index-by :key))
        combined-setting-index (merge-with merge
                                           existing-setting-index
                                           updated-setting-index)
        to-update (map #(model/add-metadata % current-user)
                       (vals combined-setting-index))]
    (doseq [setting to-update]
      (model/update-setting! model setting))
    (response/response {:success true})))

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin/api/update-settings" {:post #'update-settings-handler}]
    ["/admin/settings" {:get #'settings-handler}]]])

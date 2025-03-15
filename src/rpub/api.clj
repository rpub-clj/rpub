(ns rpub.api
  {:no-doc true}
  (:require [medley.core :as medley]
            [ring.util.response :as response]
            [rpub.api.impl :as api-impl]
            [rpub.lib.plugins :as plugins]
            [rpub.model :as model]))

(defn api-middleware [opts]
  (api-impl/api-middleware opts))

(defn- update-settings-handler [{:keys [model current-user body-params] :as _req}]
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

(defn- activate-plugin-handler
  [{:keys [body-params model current-user plugins] :as req}]
  (let [plugin' (model/->plugin (merge (:plugin body-params)
                                       {:activated true
                                        :sha (plugins/get-latest-sha)
                                        :current-user current-user}))
        remote-plugins (plugins/get-plugins)]
    (if-not (plugins/can-activate? plugins remote-plugins plugin')
      (-> (response/status 403)
          (assoc :body {:message "Plugin not in the list of installable plugins"
                        :plugin-label (:label plugin')}))
      (do
        (when (plugins/remote-plugin? remote-plugins plugin')
          (plugins/install! plugin' req))
        (model/update-plugin! model plugin')
        (response/response {:success true})))))

(defn- deactivate-plugin-handler [{:keys [body-params model current-user] :as _req}]
  (let [plugin' (model/->plugin (merge (:plugin body-params)
                                       {:activated false
                                        :current-user current-user}))]
    (plugins/uninstall! plugin')
    (model/update-plugin! model plugin')
    (response/response {:success true})))

(defn- restart-server-handler [_]
  (future
    (Thread/sleep 1000)
    (System/exit 0))
  (response/response {:success true}))

(defn routes [opts]
  [["/api" {:middleware (api-impl/api-middleware opts)}
    ["/update-settings" {:post #'update-settings-handler}]
    ["/activate-plugin" {:post #'activate-plugin-handler}]
    ["/deactivate-plugin" {:post #'deactivate-plugin-handler}]
    ["/restart-server" {:post #'restart-server-handler}]]])

(ns rpub.api
  {:no-doc true}
  (:require [clojure.edn :as edn]
            [ring.util.response :as response]
            [rpub.api.impl :as api-impl]
            [rpub.lib.plugins :as plugins]
            [rpub.model :as model]))

(defn api-middleware [opts]
  (api-impl/api-middleware opts))

(defn- update-setting-handler [{:keys [model body-params] :as _req}]
  (let [{:keys [setting-key setting-value]} body-params
        setting-key' (edn/read-string setting-key)
        [existing-setting] (model/get-settings model {:keys [setting-key']})
        updated-setting (assoc existing-setting :value setting-value)]
    (model/update-setting! model updated-setting)
    (response/response {:success true})))

(defn- activate-plugin-handler
  [{:keys [body-params model current-user plugins] :as req}]
  (let [plugin' (model/->plugin (merge (-> (:plugin body-params)
                                           (update :key edn/read-string))
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
  (let [plugin' (model/->plugin (merge (-> (:plugin body-params)
                                           (update :key edn/read-string))
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
    ["/update-setting" {:post update-setting-handler}]
    ["/activate-plugin" {:post activate-plugin-handler}]
    ["/deactivate-plugin" {:post deactivate-plugin-handler}]
    ["/restart-server" {:post restart-server-handler}]]])

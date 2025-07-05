(ns rpub.plugins.admin.plugins
  {:no-doc true}
  (:require [ring.util.response :as response]
            [rpub.lib.html :as html]
            [rpub.lib.plugins :as plugins]
            [rpub.model :as model]
            [rpub.model.app :as-alias model-app]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- plugins-handler [{:keys [plugins] :as req}]
  (let [available-plugins (->> (plugins/get-plugins)
                               (map #(select-keys % [:key :label :description])))
        current-plugins (->> plugins
                             (filter model/plugin-visible?)
                             (map #(select-keys % [:key :label :activated])))]
    (helpers/page-response
      req
      {:title "Plugins"
       :primary
       (html/custom-element
         [:plugins-page
          {:current-plugins current-plugins
           :available-plugins available-plugins}])})))

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

(defn- deactivate-plugin-handler
  [{:keys [body-params model current-user] :as _req}]
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

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin/api/activate-plugin" {:post #'activate-plugin-handler}]
    ["/admin/api/deactivate-plugin" {:post #'deactivate-plugin-handler}]
    ["/admin/api/restart-server" {:post #'restart-server-handler}]
    ["/admin/plugins" {:get #'plugins-handler}]]])

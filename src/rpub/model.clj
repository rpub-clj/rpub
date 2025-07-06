(ns rpub.model
  {:no-doc true}
  (:require [rads.inflections :as inflections]
            [rpub.model.apps :as apps]
            [rpub.model.common :as common]
            [rpub.model.plugins :as plugins]
            [rpub.model.settings :as settings]
            [rpub.model.themes :as themes]
            [rpub.model.unsaved-changes :as unsaved-changes]
            [rpub.model.users :as users]))

(def add-metadata common/add-metadata)

^:clj-reload/keep
(defprotocol Model
  (db-info [model])
  (migrate! [model opts]))

(defn ->slug [title]
  (inflections/parameterize title))

(defmulti model-impl :db-type)

(defn ->model [params]
  (let [models {:models {:themes (themes/->model params)
                         :settings (settings/->model params)
                         :users (users/->model params)
                         :apps (apps/->model params)
                         :plugins (plugins/->model params)
                         :unsaved-changes (unsaved-changes/->model params)}}
        params' (merge params models)]
    (model-impl params')))

(defn add-app-id [model app-id]
  (-> model
      (assoc :app-id app-id)
      (update :models update-vals #(assoc % :app-id app-id))))

(defn seed! [model {:keys [app] :as opts}]
  (let [model' (add-app-id model (:id app))]
    (apps/create-app! model' app)
    (users/create-user! model' (:new-user app))
    (doseq [setting (settings/initial-settings opts)]
      (settings/create-setting! model' setting))
    (doseq [plugin (plugins/initial-plugins opts)]
      (plugins/update-plugin! model' plugin))))

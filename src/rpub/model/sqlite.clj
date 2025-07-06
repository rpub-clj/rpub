(ns rpub.model.sqlite
  {:no-doc true}
  (:require [rads.migrate :as migrate]
            [rpub.model :as model]
            [rpub.model.apps.sqlite]
            [rpub.model.plugins.sqlite]
            [rpub.model.settings.sqlite]
            [rpub.model.sqlite.migrations :as migrations]
            [rpub.model.themes.sqlite]
            [rpub.model.unsaved-changes.sqlite]
            [rpub.model.users.sqlite]))

(defrecord Model [db-type ds app-id models]
  model/Model
  (db-info [model]
    (select-keys model [:db-type :ds :app-id]))

  (migrate! [model opts]
    (migrate/migrate! (migrations/config model opts))))

(defn ->model [params]
  (let [valid-keys [:db-type :ds :app-id :models]
        params' (select-keys params valid-keys)]
    (map->Model params')))

(defmethod model/model-impl :sqlite [opts]
  (->model opts))

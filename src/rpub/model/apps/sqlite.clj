(ns rpub.model.apps.sqlite
  {:no-doc true}
  (:require [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model.apps :as model]))

(defn app->row [app]
  (-> app
      (update :domains pr-str)
      (select-keys [:id :domains :created-at :created-by :updated-at
                    :updated-by])))

(defn row->app [row]
  (-> row
      (update :id parse-uuid)
      (update :domains edn/read-string)))

(defrecord Model [db-type ds app-id apps-table]
  model/Model
  (-get-apps [_ {:keys [domains]}]
    (assert (not app-id))
    (cond->> (->> (db/execute! ds {:select [:*] :from apps-table})
                  (map row->app))
      (seq domains)
      (filter #(some (set (:domains %)) domains))))

  (-create-app! [_ app]
    (db/execute-one! ds {:insert-into apps-table
                         :values [(app->row app)]})))

(defn ->model [params]
  (let [defaults {:apps-table :apps}
        valid-keys [:db-type :ds :app-id :apps-table]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

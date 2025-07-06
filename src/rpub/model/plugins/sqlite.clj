(ns rpub.model.plugins.sqlite
  {:no-doc true}
  (:require [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model.common :as common]
            [rpub.model.plugins :as model]))

(defn- row->plugin [row]
  (-> (common/row->metadata row)
      (update :key edn/read-string)
      (update :activated db/int->bool)))

(defn- plugin->row [plugin]
  (-> plugin
      (select-keys [:id :app-id :key :activated :created-at :created-by
                    :updated-at :updated-by])
      (update :key str)))

(defrecord Model [db-type ds app-id plugins-table]
  model/Model
  (-get-plugins [_ {:keys [keys]}]
    (assert app-id)
    (let [or-constraints (cond-> []
                           keys (conj [:in :key (map name keys)]))
          sql (cond-> {:select [:*]
                       :from plugins-table
                       :where [:= :app-id app-id]}
                (seq or-constraints)
                (update :where (fn [x] [:and x (db/sql-or or-constraints)])))]
      (->> (db/execute! ds sql)
           (map row->plugin))))

  (-update-plugin! [_ plugin]
    (assert app-id)
    (let [plugin' (assoc plugin :app-id app-id)]
      (db/execute-one! ds {:insert-into plugins-table
                           :values [(plugin->row plugin')]
                           :on-conflict [:app-id :key]
                           :do-update-set [:activated]}))))

(defn ->model [params]
  (let [defaults {:plugins-table :plugins}
        valid-keys [:db-type :ds :app-id :plugins-table]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

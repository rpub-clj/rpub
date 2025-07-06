(ns rpub.model.settings.sqlite
  {:no-doc true}
  (:require [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model.common :as common]
            [rpub.model.settings :as model]))

(defn- row->setting [row]
  (-> (common/row->metadata row)
      (update :key edn/read-string)))

(defn- setting->row [setting]
  (-> setting
      (update :key str)))

(defrecord Model [db-type ds app-id settings-table]
  model/Model
  (-get-settings [_ opts]
    (assert app-id)
    (let [ks (get opts :keys)
          or-constraints (cond-> []
                           ks (conj [:in :key (map str ks)]))
          sql (cond-> {:select [:*]
                       :from settings-table
                       :where [:= :app-id app-id]}
                (seq or-constraints)
                (update :where (fn [x] [:and x (db/sql-or or-constraints)])))]
      (->> (db/execute! ds sql)
           (map row->setting))))

  (-create-setting! [_ setting]
    (assert app-id)
    (let [setting' (assoc setting :app-id app-id)]
      (db/execute-one! ds {:insert-into settings-table
                           :values [(setting->row setting')]})))

  (-update-setting! [_ setting]
    (assert app-id)
    (let [setting' (assoc setting :app-id app-id)]
      (db/execute-one! ds {:insert-into settings-table
                           :values [(setting->row setting')]
                           :on-conflict [:app-id :key]
                           :do-update-set [:value :updated-by :updated-at]}))))

(defn ->model [params]
  (let [defaults {:settings-table :settings}
        valid-keys [:db-type :ds :app-id :settings-table]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

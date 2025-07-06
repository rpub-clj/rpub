(ns rpub.model.themes.sqlite
  {:no-doc true}
  (:require [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model.common :as common]
            [rpub.model.themes :as model]))

(defn- theme->row [theme]
  (update theme :value pr-str))

(defn- row->theme [row]
  (-> (common/row->metadata row)
      (update :value edn/read-string)))

(defrecord Model [db-type ds app-id themes-table]
  model/Model
  (-get-themes [_ {:keys [ids] :as _opts}]
    (assert app-id)
    (let [or-constraints (cond-> []
                           ids (conj [:in :id ids]))
          sql (cond-> {:select [:*]
                       :from themes-table
                       :where [:= :app-id app-id]}
                (seq or-constraints)
                (update :where (fn [x] [:and x (db/sql-or or-constraints)])))]
      (->> (db/execute! ds sql)
           (map row->theme))))

  (-create-theme! [_ theme]
    (assert app-id)
    (let [theme' (assoc theme :app-id app-id)]
      (db/execute-one! ds {:insert-into themes-table
                           :values [(theme->row theme')]})))

  (-update-theme! [_ theme]
    (assert app-id)
    (let [theme' (assoc theme :app-id app-id)]
      (db/execute-one! ds {:insert-into themes-table
                           :values [(theme->row theme')]
                           :on-conflict [:id]
                           :do-update-set [:label :value :updated-at :updated-by]})))

  (-delete-theme! [_ theme]
    (assert app-id)
    (db/execute-one! ds {:delete-from themes-table
                         :where [:and
                                 [:= :app-id app-id]
                                 [:= :id (:id theme)]]})))

(defn ->model [params]
  (let [defaults {:themes-table :themes}
        valid-keys [:db-type :ds :app-id :themes-table]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

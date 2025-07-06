(ns rpub.model.unsaved-changes.sqlite
  {:no-doc true}
  (:require [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model.common :as common]
            [rpub.model.unsaved-changes :as model]))

(defn row->unsaved-changes [row]
  (-> (common/row->metadata row)
      (update :key edn/read-string)
      (update :value edn/read-string)))

(defn unsaved-changes->row [unsaved-changes]
  (-> unsaved-changes
      (select-keys [:id :user-id :client-id :key :value :created-at :created-by
                    :updated-at :updated-by])
      (update :key pr-str)
      (update :value pr-str)))

(defrecord Model [db-type ds app-id unsaved-changes-table]
  model/Model
  (-get-unsaved-changes [_ {:keys [user-ids client-ids keys] :as _opts}]
    (assert app-id)
    (let [constraints (cond-> [[:in :user-id user-ids]
                               [:in :key (map pr-str keys)]]
                        (seq client-ids) (conj [:in :client-id client-ids]))]
      (->> (db/execute! ds {:select [:*]
                            :from unsaved-changes-table
                            :where (db/sql-and constraints)})
           (map row->unsaved-changes))))

  (-update-unsaved-changes! [_ unsaved-changes]
    (assert app-id)
    (let [unsaved-changes' (assoc unsaved-changes :app-id app-id)]
      (db/execute-one! ds {:insert-into unsaved-changes-table
                           :values [(unsaved-changes->row unsaved-changes')]
                           :on-conflict [:user-id :client-id :key]
                           :do-update-set [:value :updated-at :updated-by]})))

  (-delete-unsaved-changes! [_ {:keys [user-ids keys] :as _opts}]
    (assert app-id)
    (let [constraints [[:in :user-id user-ids]
                       [:in :key (map pr-str keys)]]]
      (db/execute-one! ds {:delete-from unsaved-changes-table
                           :where (db/sql-and constraints)}))))

(defn ->model [params]
  (let [defaults {:unsaved-changes-table :unsaved-changes}
        valid-keys [:db-type :ds :app-id :unsaved-changes-table]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

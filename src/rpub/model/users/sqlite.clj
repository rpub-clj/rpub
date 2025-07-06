(ns rpub.model.users.sqlite
  {:no-doc true}
  (:require [medley.core :as medley]
            [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model.users :as model])
  (:import (java.time Instant)))

(defn row->metadata [row]
  (cond-> row
    (:id row) (update :id parse-uuid)
    (:app-id row) (update :app-id parse-uuid)
    (:user-id row) (update :user-id parse-uuid)
    (:created-at row) (update :created-at #(Instant/parse %))
    (:created-by row) (update :created-by parse-uuid)
    (:updated-at row) (update :updated-at #(Instant/parse %))
    (:updated-by row) (update :updated-by parse-uuid)))

(defn- row->user [row]
  (row->metadata row))

(defn- user->row [user]
  (select-keys user [:id :app-id :username :password-hash :created-at
                     :created-by :updated-at :updated-by]))

(defn- row->role [row]
  (-> (row->metadata row)
      (update :permissions edn/read-string)))

(defn- role->row [role]
  (-> role
      (select-keys [:id :app-id :label :permissions :created-at :created-by
                    :updated-at :updated-by])
      (update :permissions pr-str)))

(defn- user-role->row [user-role]
  user-role)

(defn- row->user-role [row]
  (-> (row->metadata row)
      (update :role-id parse-uuid)))

(defn- add-roles [users {:keys [roles user-roles]}]
  (let [user-roles-index (group-by :user-id user-roles)
        roles-index (medley/index-by :id roles)]
    (map (fn [user]
           (let [ur (get user-roles-index (:id user))
                 r (map #(get roles-index (:role-id %)) ur)]
             (assoc user :roles r)))
         users)))

(defn- get-users*
  [{:keys [app-id ds users-table] :as model}
   {:keys [ids usernames password] :as opts}]
  (assert app-id)
  (let [or-constraints (cond-> []
                         ids (conj [:in :id ids])
                         usernames (conj [:in :username usernames]))
        sql (cond-> {:select [:*]
                     :from users-table
                     :where [:= :app-id app-id]}
              (seq or-constraints)
              (update :where (fn [x] [:and x (db/sql-or or-constraints)])))
        users (->> (db/execute! ds sql)
                   (map row->user)
                   (map #(if password % (dissoc % :password-hash))))
        user-roles (when (:roles opts)
                     (model/get-user-roles
                       model
                       {:user-ids (map :id users)}))
        roles (when (:roles opts)
                (model/get-roles
                  model
                  {:ids (map :role-id user-roles)}))]
    (cond-> users
      (:roles opts) (add-roles {:roles roles, :user-roles user-roles}))))

(defrecord Model [db-type ds app-id apps-table users-table roles-table
                  user-roles-table]
  model/Model
  (-get-users [model opts]
    (get-users* model opts))

  (-create-user! [_ user]
    (assert app-id)
    (let [user' (assoc user :app-id app-id)]
      (db/execute-one! ds {:insert-into users-table
                           :values [(user->row user')]})))

  (-get-roles [_ {:keys [ids labels] :as _opts}]
    (assert app-id)
    (let [or-constraints (cond-> []
                           ids (conj [:in :id ids])
                           labels (conj [:in :label labels]))
          sql (cond-> {:select [:*]
                       :from roles-table
                       :where [:= :app-id app-id]}
                (seq or-constraints)
                (update :where (fn [x] [:and x (db/sql-or or-constraints)])))]
      (->> (db/execute! ds sql)
           (map row->role))))

  (-create-role! [_ role]
    (assert app-id)
    (let [role' (assoc role :app-id app-id)]
      (db/execute-one! ds {:insert-into roles-table
                           :values [(role->row role')]})))

  (-get-user-roles [_ {:keys [user-ids role-ids] :as _opts}]
    (assert app-id)
    (let [or-constraints (cond-> []
                           user-ids (conj [:in :user-id user-ids])
                           role-ids (conj [:in :role-id role-ids]))
          sql (cond-> {:select [:*]
                       :from user-roles-table
                       :where [:= :app-id app-id]}
                (seq or-constraints)
                (update :where (fn [x] [:and x (db/sql-or or-constraints)])))]
      (->> (db/execute! ds sql)
           (map row->user-role))))

  (-create-user-role! [_ user-role]
    (assert app-id)
    (db/execute-one! ds {:insert-into user-roles-table
                         :values [(user-role->row user-role)]})))

(defn ->model [params]
  (let [defaults {:apps-table :apps
                  :users-table :users
                  :plugins-table :plugins
                  :unsaved-changes-table :unsaved-changes
                  :roles-table :roles
                  :user-roles-table :user-roles}
        valid-keys [:db-type :ds :app-id :apps-table :users-table
                    :settings-model :plugins-table :unsaved-changes-table
                    :roles-table :user-roles-table :themes-model]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

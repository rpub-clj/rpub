(ns rpub.model.sqlite
  {:no-doc true}
  (:require [medley.core :as medley]
            [rads.migrate :as migrate]
            [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model :as model]
            [rpub.model.sqlite.migrations :as migrations])
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

(defn- row->setting [row]
  (-> (row->metadata row)
      (update :key edn/read-string)))

(defn- setting->row [setting]
  (-> setting
      (update :key str)))

(defn- row->plugin [row]
  (-> (row->metadata row)
      (update :key edn/read-string)
      (update :activated db/int->bool)))

(defn- plugin->row [plugin]
  (-> plugin
      (select-keys [:id :app-id :key :activated :created-at :created-by
                    :updated-at :updated-by])
      (update :key str)))

(defn row->unsaved-changes [row]
  (-> (row->metadata row)
      (update :key edn/read-string)
      (update :value edn/read-string)))

(defn unsaved-changes->row [unsaved-changes]
  (-> unsaved-changes
      (select-keys [:id :user-id :client-id :key :value :created-at :created-by
                    :updated-at :updated-by])
      (update :key pr-str)
      (update :value pr-str)))

(defn app->row [app]
  (-> app
      (update :domains pr-str)
      (select-keys [:id :domains :created-at :created-by :updated-at
                    :updated-by])))

(defn row->app [row]
  (-> row
      (update :id parse-uuid)
      (update :domains edn/read-string)))

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

(defn- theme->row [theme]
  (update theme :value pr-str))

(defn- row->theme [row]
  (-> (row->metadata row)
      (update :value edn/read-string)))

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

(defrecord Model [db-type ds app-id apps-table users-table settings-table
                  plugins-table unsaved-changes-table roles-table
                  user-roles-table themes-table]
  model/Model
  (db-info [model]
    (select-keys model [:db-type :ds :app-id]))

  (migrate! [model opts]
    (migrate/migrate! (migrations/config model opts)))

  (get-users [model opts]
    (get-users* model opts))

  (create-user! [_ user]
    (assert app-id)
    (let [user' (assoc user :app-id app-id)]
      (db/execute-one! ds {:insert-into users-table
                           :values [(user->row user')]})))

  (get-settings [_ opts]
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

  (create-setting! [_ setting]
    (assert app-id)
    (let [setting' (assoc setting :app-id app-id)]
      (db/execute-one! ds {:insert-into settings-table
                           :values [(setting->row setting')]})))

  (update-setting! [_ setting]
    (assert app-id)
    (let [setting' (assoc setting :app-id app-id)]
      (db/execute-one! ds {:insert-into settings-table
                           :values [(setting->row setting')]
                           :on-conflict [:app-id :key]
                           :do-update-set [:value :updated-by :updated-at]})))

  (get-plugins [_ {:keys [keys]}]
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

  (update-plugin! [_ plugin]
    (assert app-id)
    (let [plugin' (assoc plugin :app-id app-id)]
      (db/execute-one! ds {:insert-into plugins-table
                           :values [(plugin->row plugin')]
                           :on-conflict [:app-id :key]
                           :do-update-set [:activated]})))

  (get-unsaved-changes [_ {:keys [user-ids client-ids keys] :as _opts}]
    (assert app-id)
    (let [constraints (cond-> [[:in :user-id user-ids]
                               [:in :key (map pr-str keys)]]
                        (seq client-ids) (conj [:in :client-id client-ids]))]
      (->> (db/execute! ds {:select [:*]
                            :from unsaved-changes-table
                            :where (db/sql-and constraints)})
           (map row->unsaved-changes))))

  (update-unsaved-changes! [_ unsaved-changes]
    (assert app-id)
    (let [unsaved-changes' (assoc unsaved-changes :app-id app-id)]
      (db/execute-one! ds {:insert-into unsaved-changes-table
                           :values [(unsaved-changes->row unsaved-changes')]
                           :on-conflict [:user-id :client-id :key]
                           :do-update-set [:value :updated-at :updated-by]})))

  (delete-unsaved-changes! [_ {:keys [user-ids keys] :as _opts}]
    (assert app-id)
    (let [constraints [[:in :user-id user-ids]
                       [:in :key (map pr-str keys)]]]
      (db/execute-one! ds {:delete-from unsaved-changes-table
                           :where (db/sql-and constraints)})))

  (get-apps [_ {:keys [domains]}]
    (assert (not app-id))
    (cond->> (->> (db/execute! ds {:select [:*] :from apps-table})
                  (map row->app))
      (seq domains)
      (filter #(some (set (:domains %)) domains))))

  (create-app! [_ app]
    (db/execute-one! ds {:insert-into apps-table
                         :values [(app->row app)]}))

  (get-roles [_ {:keys [ids labels] :as _opts}]
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

  (create-role! [_ role]
    (assert app-id)
    (let [role' (assoc role :app-id app-id)]
      (db/execute-one! ds {:insert-into roles-table
                           :values [(role->row role')]})))

  (get-user-roles [_ {:keys [user-ids role-ids] :as _opts}]
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

  (create-user-role! [_ user-role]
    (assert app-id)
    (db/execute-one! ds {:insert-into user-roles-table
                         :values [(user-role->row user-role)]}))

  (get-themes [_ {:keys [ids] :as _opts}]
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

  (create-theme! [_ theme]
    (assert app-id)
    (let [theme' (assoc theme :app-id app-id)]
      (db/execute-one! ds {:insert-into themes-table
                           :values [(theme->row theme')]}))))

(defn ->model [params]
  (let [defaults {:apps-table :apps
                  :users-table :users
                  :settings-table :settings
                  :plugins-table :plugins
                  :unsaved-changes-table :unsaved-changes
                  :roles-table :roles
                  :user-roles-table :user-roles
                  :themes-table :themes}
        valid-keys [:db-type :ds :app-id :apps-table :users-table
                    :settings-table :plugins-table :unsaved-changes-table
                    :roles-table :user-roles-table :themes-table]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

(comment
  (def test-model
    (model/->model {:db-type :sqlite
                    :ds (db/get-datasource "jdbc:sqlite:data/app.db")}))

  @(def test-unsaved-changes
     (model/get-unsaved-changes
       test-model
       {:user-id #uuid"0c12755b-537f-4199-88cb-55c47ddecc67"}))

  (model/update-unsaved-changes!
    test-model
    (assoc test-unsaved-changes :value {:a 1})))

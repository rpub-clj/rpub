(ns rpub.model.sqlite
  {:no-doc true}
  (:require [rads.migrate :as migrate]
            [rpub.lib.db :as db]
            [rpub.lib.edn :as edn]
            [rpub.model :as model]
            [rpub.model.sqlite.migrations :as migrations])
  (:import (java.time Instant)))

(defn row->metadata [row]
  (cond-> row
    (:id row) (update :id parse-uuid)
    (:created-at row) (update :created-at #(Instant/parse %))
    (:created-by row) (update :created-by parse-uuid)
    (:updated-at row) (update :updated-at #(Instant/parse %))
    (:updated-by row) (update :updated-by parse-uuid)))

(defn- row->user [row]
  (-> (row->metadata row)
      (update :app-id parse-uuid)))

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
      (update :activated db/int->bool)
      (update :app-id parse-uuid)))

(defn- plugin->row [plugin]
  (-> plugin
      (select-keys [:id :app-id :key :activated :created-at :created-by
                    :updated-at :updated-by])
      (update :key str)))

(defn row->unsaved-changes [row]
  (-> (row->metadata row)
      (update :user-id parse-uuid)
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

(defn sql-or [constraints]
  (if (= (count constraints) 1)
    (first constraints)
    (into [:or] constraints)))

(defn sql-and [constraints]
  (if (= (count constraints) 1)
    (first constraints)
    (into [:and] constraints)))

(defrecord Model [db-type ds app-id apps-table users-table settings-table
                  plugins-table unsaved-changes-table]
  model/Model
  (db-info [model]
    (select-keys model [:db-type :ds :app-id]))

  (migrate! [model opts]
    (migrate/migrate! (migrations/config model opts)))

  (get-users [_ {:keys [ids usernames password]}]
    (assert app-id)
    (let [or-constraints (cond-> []
                           ids (conj [:in :id ids])
                           usernames (conj [:in :username usernames]))
          sql (cond-> {:select [:*]
                       :from users-table
                       :where [:= :app-id app-id]}
                (seq or-constraints)
                (update :where (fn [x] [:and x (sql-or or-constraints)])))]
      (->> (db/execute! ds sql)
           (map row->user)
           (map #(if password % (dissoc % :password-hash))))))

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
                (update :where (fn [x] [:and x (sql-or or-constraints)])))]
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
                (update :where (fn [x] [:and x (sql-or or-constraints)])))]
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
                            :where (sql-and constraints)})
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
                           :where (sql-and constraints)})))

  (get-apps [_ {:keys [domains]}]
    (assert (not app-id))
    (cond->> (->> (db/execute! ds {:select [:*] :from apps-table})
                  (map row->app))
      (seq domains)
      (filter #(some (set (:domains %)) domains))))

  (create-app! [_ app]
    (db/execute-one! ds {:insert-into apps-table
                         :values [(app->row app)]})))

(defn ->model [params]
  (let [defaults {:apps-table :apps
                  :users-table :users
                  :settings-table :settings
                  :plugins-table :plugins
                  :unsaved-changes-table :unsaved-changes}
        valid-keys [:db-type :ds :app-id :apps-table :users-table
                    :settings-table :plugins-table :unsaved-changes-table]
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

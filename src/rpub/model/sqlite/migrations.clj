(ns rpub.model.sqlite.migrations
  (:require [honey.sql.helpers :as h]
            [rads.migrate.next-jdbc :as migrate-next-jdbc]
            [rpub.lib.db :as db]))

(defn- initial-schema [{:keys [users-table settings-table plugins-table] :as _model}]
  [(db/strict
     {:create-table [users-table :if-not-exists]
      :with-columns (concat [(db/uuid-column :id [:primary-key] [:not nil])
                             [:username :text [:not nil]]
                             [:password-hash :text [:not nil]]
                             [:display-name :text]]
                            db/audit-columns)})

   {:create-index [[:unique (db/index-name users-table :username) :if-not-exists]
                   [users-table :username]]}

   (db/strict
     {:create-table [settings-table :if-not-exists]
      :with-columns (concat [(db/uuid-column :id [:primary-key] [:not nil])
                             [:key :text [:not nil]]
                             [:value :text [:not nil]]
                             [:label :text [:not nil]]
                             [:description :text]]
                            db/audit-columns)})

   {:create-index [[:unique (db/index-name settings-table :key) :if-not-exists]
                   [:settings :key]]}

   (db/strict
     {:create-table [plugins-table :if-not-exists]
      :with-columns (concat [(db/uuid-column :id [:primary-key] [:not nil])
                             [:key :text [:not nil]]
                             (db/boolean-column :activated [:not nil])]
                            db/audit-columns)})

   {:create-index [[:unique (db/index-name plugins-table :key) :if-not-exists]
                   [:plugins :key]]}])

(defn- unsaved-changes-table-schema [{:keys [unsaved-changes-table] :as _model}]
  [(db/strict
     {:create-table [unsaved-changes-table :if-not-exists]
      :with-columns (concat [(db/uuid-column :id [:primary-key] [:not nil])
                             [:user-id :text [:not nil]]
                             [:client-id :text [:not nil]]
                             [:key :text [:not nil]]
                             [:value :text [:not nil]]]
                            db/audit-columns)})

   {:create-index [[:unique (db/index-name unsaved-changes-table :key)
                    :if-not-exists]
                   [unsaved-changes-table :user-id :client-id :key]]}])

(defn- apps-table-schema [{:keys [apps-table] :as _model}]
  [(db/strict
     {:create-table apps-table
      :with-columns (concat [(db/uuid-column :id [:primary-key] [:not nil])
                             [:domains :text [:not nil]]]
                            db/audit-columns)})])

(defn- apps-table-migration [{:keys [apps-table] :as model}]
  {:id :apps-table
   :migrate (fn [{:keys [tx]}]
              (doseq [stmt (apps-table-schema model)]
                (db/execute-one! tx stmt)))
   :rollback (fn [{:keys [tx]}]
               (db/execute-one! tx {:drop-table apps-table}))})

(defn- app-id-columns-migration
  [{:keys [settings-table plugins-table users-table] :as model}]
  (let [tables ((juxt :settings-table :plugins-table :users-table) model)]
    {:id :app-id-columns
     :migrate (fn [{:keys [tx]}]
                (doseq [table tables]
                  (db/execute-one!
                    tx
                    (-> (h/alter-table table)
                        (h/add-column :app-id :text))))
                (db/execute-one!
                  tx
                  (h/drop-index (db/index-name users-table :username)))
                (db/execute-one!
                  tx
                  {:create-index [[:unique (db/index-name users-table :username) :if-not-exists]
                                  [users-table :app-id :username]]})
                (db/execute-one!
                  tx
                  (h/drop-index (db/index-name settings-table :key)))
                (db/execute-one!
                  tx
                  {:create-index [[:unique (db/index-name settings-table :key) :if-not-exists]
                                  [settings-table :app-id :key]]})
                (db/execute-one!
                  tx
                  (h/drop-index (db/index-name plugins-table :key)))
                (db/execute-one!
                  tx
                  {:create-index [[:unique (db/index-name plugins-table :key) :if-not-exists]
                                  [plugins-table :app-id :key]]}))
     :rollback (fn [{:keys [tx]}]
                 (doseq [table tables]
                   (db/execute-one!
                     tx
                     (-> (h/alter-table table)
                         (h/drop-column :app-id)))))}))

(defn- initial-schema-migration
  [{:keys [users-table settings-table plugins-table]
    :as model}]
  {:id :initial-schema
   :migrate (fn [{:keys [tx]}]
              (doseq [stmt (initial-schema model)]
                (db/execute-one! tx stmt)))
   :rollback (fn [{:keys [tx]}]
               (doseq [table [users-table settings-table plugins-table]]
                 (db/execute-one! tx {:drop-table table})))})

(defn- unsaved-changes-table-migration
  [{:keys [unsaved-changes-table] :as model}]
  {:id :unsaved-changes-table
   :migrate (fn [{:keys [tx]}]
              (doseq [stmt (unsaved-changes-table-schema model)]
                (db/execute-one! tx stmt)))
   :rollback (fn [{:keys [tx]}]
               (db/execute-one! tx {:drop-table unsaved-changes-table}))})

(defn- roles-tables-schema
  [{:keys [roles-table user-roles-table] :as _model}]
  [(db/strict
     {:create-table [roles-table :if-not-exists]
      :with-columns (concat [(db/uuid-column :id [:primary-key] [:not nil])
                             [:label :text [:not nil]]
                             [:app-id :text [:not nil]]]
                            db/audit-columns)})

   {:create-index [[:unique (db/index-name roles-table :label) :if-not-exists]
                   [roles-table :app-id :label]]}

   (db/strict
     {:create-table [user-roles-table :if-not-exists]
      :with-columns (concat [(db/uuid-column :id [:primary-key] [:not nil])
                             [:user-id :text [:not nil]]
                             [:role-id :text [:not nil]]
                             [:app-id :text [:not nil]]]
                            db/audit-columns)})

   {:create-index [[:unique (db/index-name user-roles-table :user-role) :if-not-exists]
                   [user-roles-table :app-id :user-id :role-id]]}])

(defn- roles-tables-migration
  [{:keys [roles-table user-roles-table] :as model}]
  {:id :roles-and-permissions-tables
   :migrate (fn [{:keys [tx]}]
              (doseq [stmt (roles-tables-schema model)]
                (db/execute-one! tx stmt)))
   :rollback (fn [{:keys [tx]}]
               (doseq [table [user-roles-table roles-table]]
                 (db/execute-one! tx {:drop-table table})))})

(defn- migrations [model _opts]
  [(initial-schema-migration model)
   (unsaved-changes-table-migration model)
   (apps-table-migration model)
   (app-id-columns-migration model)
   (roles-tables-migration model)])

(defn config [model opts]
  {:migrations (migrations model opts)
   :storage (migrate-next-jdbc/storage model)})

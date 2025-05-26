(ns rpub.plugins.content-types.sqlite.migrations
  (:require [honey.sql.helpers :as h]
            [rads.migrate.next-jdbc :as migrate-next-jdbc]
            [rpub.lib.db :as db]))

(defn- initial-schema
  [{:keys [fields-table
           content-types-table
           content-type-fields-table
           content-items-table]}]
  [(db/strict
     {:create-table [fields-table :if-not-exists]
      :with-columns
      (concat [(db/uuid-column :id [:primary-key] [:not nil])
               [:name :text [:not nil]]
               [:type :text [:not nil]]]
              db/audit-columns)})

   (db/strict
     {:create-table [content-types-table :if-not-exists]
      :with-columns
      (concat [(db/uuid-column :id [:primary-key] [:not nil])
               [:name :text [:not nil]]
               [:slug :text [:not nil]]]
              db/audit-columns)})

   (db/strict
     {:create-table [content-type-fields-table :if-not-exists]
      :with-columns
      (concat [(db/uuid-column :id [:primary-key] [:not nil])
               (-> (db/uuid-column :content-type-id)
                   (db/references :content-types :id))
               (-> (db/uuid-column :field-id)
                   (db/references :fields :id))
               [:rank :integer [:not nil]]]
              db/audit-columns)})

   (db/strict
     {:create-table [content-items-table :if-not-exists]
      :with-columns
      (concat [(db/uuid-column :id [:primary-key] [:not nil])
               (-> (db/uuid-column :content-type-id)
                   (db/references :content-types :id))
               [:document :text :not nil]]
              db/audit-columns)})])

(defn- app-id-columns-migration [model]
  (let [tables ((juxt :fields-table :content-types-table) model)]
    {:id :app-id-columns
     :migrate (fn [{:keys [tx]}]
                (doseq [table tables]
                  (db/execute-one!
                    tx
                    (-> (h/alter-table table)
                        (h/add-column :app-id :text)))))
     :rollback (fn [{:keys [tx]}]
                 (doseq [table tables]
                   (db/execute-one!
                     tx
                     (-> (h/alter-table table)
                         (h/drop-column :app-id)))))}))

(defn- initial-schema-migration [model]
  (let [{:keys [content-types-table
                content-type-fields-table
                content-items-table
                migration-events-table]} model]
    {:id :initial-schema
     :migrate (fn [{:keys [tx]}]
                (doseq [stmt (initial-schema model)]
                  (db/execute-one! tx stmt)))
     :rollback (fn [{:keys [tx]}]
                 (doseq [table [content-types-table
                                content-type-fields-table
                                content-items-table
                                migration-events-table]]
                   (db/execute-one! tx {:drop-table table})))}))

(defn- migrations [model]
  [(initial-schema-migration model)
   (app-id-columns-migration model)])

(defn config [model]
  (let [{:keys [ds migration-events-table]} model]
    {:migrations (migrations model)
     :storage (migrate-next-jdbc/storage
                {:ds ds
                 :events-table migration-events-table})}))

(ns rpub.model.sqlite
  {:no-doc true}
  (:require [clojure.edn :as edn]
            [rads.migrate :as migrate]
            [rads.migrate.next-jdbc :as migrate-next-jdbc]
            [rpub.lib.db :as db]
            [rpub.model :as model])
  (:import (java.time Instant)))

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

(defn initial-settings
  [{:keys [encrypted-session-store-key
           site-title
           site-base-url
           current-user]
    :as _opts}]
  (->> [{:key :encrypted-session-store-key
         :label "Encrypted Session Store Key"
         :value (pr-str encrypted-session-store-key)}

        {:key :site-title
         :label "Site Title"
         :value site-title}

        {:key :site-base-url
         :label "Site URL"
         :value site-base-url}

        {:key :permalink-single
         :label "Permalinks (Single)"
         :value "/:content-type-slug/:content-item-slug"}

        {:key :theme-name
         :label "Theme Name"
         :value "Starter Theme"}

        {:key :site-description
         :label "Site Description"
         :value "Write an awesome description for your new site here."}

        {:key :site-subtitle
         :label "Site Subtitle"
         :value "Your awesome title."}

        {:key :contact-email
         :label "Contact Email"
         :value "foo@bar.com"}

        {:key :footer-links
         :label "Footer Links"
         :value (pr-str
                  [{:title "rPub", :url "http://rpub.dev"}
                   {:title "rPub Plugins", :url "https://github.com/rpub-clj/plugins"}])}

        {:key :default-content-type-slug
         :label "Default Content Type Slug"
         :value "pages"}]
       (map #(model/->setting (assoc % :current-user current-user)))))

(defn initial-plugins [{:keys [current-user] :as _opts}]
  (->> [{:key :rpub.plugins.starter-theme/plugin, :activated true}
        {:key :rpub.plugins.external-editing/plugin, :activated true}
        {:key :rpub.plugins.content-types/plugin, :activated true}]
       (map #(model/->plugin (assoc % :current-user current-user)))))

(defn- seed [model {:keys [new-user] :as opts}]
  (model/create-user! model new-user)
  (doseq [setting (initial-settings opts)]
    (model/create-setting! model setting))
  (doseq [plugin (initial-plugins opts)]
    (model/update-plugin! model plugin)))

(defn- migrations [model opts]
  (let [{:keys [users-table settings-table plugins-table]} model]
    [{:id :initial-schema
      :migrate (fn [{:keys [tx]}]
                 (doseq [stmt (initial-schema model)]
                   (db/execute-one! tx stmt)))
      :rollback (fn [{:keys [tx]}]
                  (doseq [table [users-table settings-table plugins-table]]
                    (db/execute-one! tx {:drop-table table})))}

     {:id :initial-rows
      :migrate (fn [{:keys [tx]}]
                 (let [model' (assoc model :ds tx)]
                   (seed model' opts)))
      :rollback (fn [{:keys [tx]}]
                  (doseq [table [users-table settings-table plugins-table]]
                    (db/execute-one! tx {:delete-from table})))}]))

(defn row->metadata [row]
  (cond-> row
    (:id row) (update :id parse-uuid)
    (:created-at row) (update :created-at #(Instant/parse %))
    (:created-by row) (update :created-by parse-uuid)
    (:updated-at row) (update :updated-at #(Instant/parse %))
    (:updated-by row) (update :updated-by parse-uuid)))

(defn- row->user [row]
  (row->metadata row))

(defn- user->row [user]
  user)

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
      (select-keys [:id :key :activated :created-at :created-by
                    :updated-at :updated-by])
      (update :key str)))

(defn sql-or [constraints]
  (if (= (count constraints) 1)
    (first constraints)
    (into [:or] constraints)))

(defn sql-and [constraints]
  (if (= (count constraints) 1)
    (first constraints)
    (into [:and] constraints)))

(defn- migration-config [model opts]
  {:migrations (migrations model opts)
   :storage (migrate-next-jdbc/storage model)})

(defrecord Model [db-type ds users-table settings-table plugins-table]
  model/Model
  (db-info [model]
    (select-keys model [:db-type :ds]))

  (migrate! [model opts]
    (migrate/migrate! (migration-config model opts)))

  (get-users [_ {:keys [ids usernames password]}]
    (let [constraints (cond-> []
                        ids (conj [:in :id ids])
                        usernames (conj [:in :username usernames]))
          sql (cond-> {:select [:*] :from users-table}
                (seq constraints) (assoc :where (sql-or constraints)))]
      (->> (db/execute! ds sql)
           (map row->user)
           (map #(if password % (dissoc % :password-hash))))))

  (create-user! [_ user]
    (db/execute-one! ds {:insert-into users-table
                         :values [(user->row user)]}))

  (get-settings [_ {:keys [keys]}]
    (let [constraints (cond-> []
                        keys (conj [:in :key (map str keys)]))
          sql (cond-> {:select [:*] :from settings-table}
                (seq constraints) (assoc :where (sql-or constraints)))]
      (->> (db/execute! ds sql)
           (map row->setting))))

  (create-setting! [_ setting]
    (db/execute-one! ds {:insert-into settings-table
                         :values [(setting->row setting)]}))

  (update-setting! [_ setting]
    (db/execute-one! ds {:insert-into settings-table
                         :values [(setting->row setting)]
                         :on-conflict :id
                         :do-update-set [:value]}))

  (get-plugins [_ {:keys [keys]}]
    (let [constraints (cond-> []
                        keys (conj [:in :key (map name keys)]))
          sql (cond-> {:select [:*] :from plugins-table}
                (seq constraints) (assoc :where (sql-or constraints)))]
      (->> (db/execute! ds sql)
           (map row->plugin))))

  (update-plugin! [_ plugin]
    (db/execute-one! ds {:insert-into plugins-table
                         :values [(plugin->row plugin)]
                         :on-conflict :key
                         :do-update-set [:activated]})))

(defn ->model [params]
  (let [defaults {:users-table :users
                  :settings-table :settings
                  :plugins-table :plugins}
        valid-keys [:db-type :ds :users-table :settings-table :plugins-table]
        params' (merge defaults (select-keys params valid-keys))]
    (map->Model params')))

(defmethod model/->model :sqlite [opts]
  (->model opts))

(ns rpub.plugins.content-types.sqlite
  {:no-doc true}
  (:require [babashka.json :as json]
            [clojure.set :as set]
            [medley.core :as medley]
            [rads.migrate :as migrate]
            [rads.migrate.next-jdbc :as migrate-next-jdbc]
            [rpub.lib.db :as db]
            [rpub.model :as model]
            [rpub.model.sqlite :as sqlite]
            [rpub.plugins.content-types :as content-types]))

(defn row->content-type [row]
  (-> (sqlite/row->metadata row)
      (update :slug keyword)))

(defn row->content-type-field [row]
  (-> (sqlite/row->metadata row)
      (update :content-type-id parse-uuid)
      (update :field-id parse-uuid)
      (update :field-type keyword)))

(defn row->content-item [row]
  (-> (sqlite/row->metadata row)
      (update :content-type-id parse-uuid)
      (update :document (fn [s]
                          (-> (json/read-str s {:key-fn identity})
                              (update-keys parse-uuid))))))

(defn content-type->row [content-type]
  (-> content-type
      (update :slug name)
      (dissoc :fields)))

(defn add-fields [content-types content-type-fields]
  (let [index (group-by :content-type-id content-type-fields)]
    (map (fn [content-type]
           (let [fields (as-> (get index (:id content-type)) $
                          (set/rename $ {:field-id :id
                                         :field-name :name
                                         :field-type :type
                                         :field-created-at :created-at
                                         :field-created-by :created-by
                                         :content-type-field-rank :rank})
                          (map #(dissoc % :content-type-id) $))]
             (assoc content-type :fields fields)))
         content-types)))

(defn- get-content-type-fields
  [{:keys [ds content-type-fields-table fields-table] :as _model}
   {:keys [content-type-ids] :as _opts}]
  (->> (db/execute!
         ds
         {:select [[:ctf.content-type-id :content-type-id]
                   [:ctf.rank :content-type-field-rank]
                   [:f.id :field-id]
                   [:f.name :field-name]
                   [:f.type :field-type]
                   [:f.created-at :field-created-at]
                   [:f.created-by :field-created-by]]
          :from [[content-type-fields-table :ctf]]
          :join [[fields-table :f] [:= :f.id :ctf.field-id]]
          :where [:in :content-type-id content-type-ids]})
       (map row->content-type-field)))

(defn- content-item->row [{:keys [content-type] :as content-item}]
  (-> content-item
      (update :document json/write-str)
      (assoc :content-type-id (:id content-type))
      (dissoc :content-type)))

(defn- field->row [field]
  (-> field
      (update :type name)
      (dissoc :rank)))

(defn ->content-type-field [{:keys [current-user content-type field rank] :as opts}]
  (as-> {:id (or (:id opts) (random-uuid))
         :content-type-id (:id content-type)
         :field-id (:id field)
         :rank rank} $
    (model/add-metadata $ current-user)))

(defn content-type-field->row [content-type-field]
  content-type-field)

(defn add-content-item-counts
  [{:keys [ds content-items-table] :as _model}
   content-types]
  (let [count-index (->> (db/execute!
                           ds
                           {:select [:content-type-id
                                     [[:count :*] :content-item-count]]
                            :from content-items-table
                            :group-by :content-type-id
                            :where [:in :content-type-id (map :id content-types)]})
                         (map #(update % :content-type-id parse-uuid))
                         (medley/index-by :content-type-id))]
    (map (fn [content-type]
           (let [c (get-in count-index [(:id content-type) :content-item-count] 0)]
             (assoc content-type :content-item-count c)))
         content-types)))

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

(defn default-content-types [{:keys [current-user] :as _opts}]
  (let [title-field {:id content-types/title-field-id, :name "Title", :type :text, :rank 1}
        slug-field {:id content-types/slug-field-id, :name "Slug", :type :text, :rank 2}
        content-field {:id content-types/content-field-id, :name "Content", :type :text-lg, :rank 3}]
    [(content-types/->content-type
       {:id #uuid"5bd88a30-c4dd-4b3e-b4de-ae54ac4f4338"
        :name "Pages"
        :slug :pages
        :current-user current-user
        :fields [title-field
                 slug-field
                 content-field]})
     (content-types/->content-type
       {:id #uuid"fdeb5967-84ea-41b1-a9b9-4a55874cd4c5"
        :name "Posts"
        :slug :posts
        :current-user current-user
        :fields [title-field
                 slug-field
                 content-field]})]))

(defn- content-type-field-id [content-type field-name]
  (:id (some #(when (= (:name %) field-name) %) (:fields content-type))))

(def initial-post
  {:title "Hello World!"
   :slug "hello-world"
   :content "This is the first post."})

(def initial-page
  {:title "About"
   :slug "about"
   :content "This is the about page."})

(defn- content-type-by-slug [content-types slug]
  (some #(when (= (:slug %) slug) %) content-types))

(defn default-content-items [content-types {:keys [current-user]}]
  (let [posts-type (content-type-by-slug content-types :posts)
        pages-type (content-type-by-slug content-types :pages)]
    (concat
      [(content-types/->content-item
         {:id #uuid"972f2f2c-8681-4f91-b930-025c59d1739e"
          :current-user current-user
          :content-type posts-type
          :document {(content-type-field-id posts-type "Title")
                     (:title initial-post)

                     (content-type-field-id posts-type "Slug")
                     (:slug initial-post)

                     (content-type-field-id posts-type "Content")
                     (:content initial-post)}})
       (content-types/->content-item
         {:id #uuid"0b095750-af17-4d2c-9e53-9c9728f8ebeb"
          :current-user current-user
          :content-type pages-type
          :document {(content-type-field-id pages-type "Title")
                     (:title initial-page)

                     (content-type-field-id pages-type "Slug")
                     (:slug initial-page)

                     (content-type-field-id pages-type "Content")
                     (:content initial-page)}})])))

(defn- seed [model]
  (let [content-types (default-content-types model)
        content-items (default-content-items content-types model)]
    (doseq [content-type content-types]
      (content-types/create-content-type! model content-type))
    (doseq [content-item content-items]
      (content-types/create-content-item! model content-item))))

(defn- migrations [model]
  (let [{:keys [content-types-table
                content-type-fields-table
                content-items-table
                migration-events-table]} model]
    [{:id :initial-schema
      :migrate (fn [{:keys [tx]}]
                 (doseq [stmt (initial-schema model)]
                   (db/execute-one! tx stmt)))
      :rollback (fn [{:keys [tx]}]
                  (doseq [table [content-types-table
                                 content-type-fields-table
                                 content-items-table
                                 migration-events-table]]
                    (db/execute-one! tx {:drop-table table})))}

     {:id :initial-rows
      :migrate (fn [{:keys [tx]}]
                 (let [model' (assoc model :ds tx)]
                   (seed model')))
      :rollback (fn [{:keys [tx]}]
                  (doseq [table [content-types-table
                                 content-type-fields-table
                                 content-items-table
                                 migration-events-table]]
                    (db/execute-one! tx {:delete-from table})))}]))

(defn- migration-config [model]
  (let [{:keys [ds migration-events-table]} model]
    {:migrations (migrations model)
     :storage (migrate-next-jdbc/storage
                {:ds ds
                 :events-table migration-events-table})}))

(defrecord Model
           [ds
            current-user
            fields-table
            content-types-table
            content-type-fields-table
            content-items-table
            migration-events-table]
  content-types/Model
  (migrate! [model]
    (migrate/migrate! (migration-config model)))

  (get-content-types
    [model
     {:keys [content-type-ids
             content-type-slugs
             count-items]}]
    (let [constraints (cond-> []
                        content-type-ids (conj [:in :id content-type-ids])
                        content-type-slugs (conj [:in :slug (map name content-type-slugs)]))
          sql (cond-> {:select [:id :name :slug :created-at :created-by :updated-at :updated-by]
                       :from content-types-table}
                (seq constraints) (assoc :where (sqlite/sql-or constraints)))
          content-types (->> (db/execute! ds sql)
                             (map row->content-type))
          content-type-fields (get-content-type-fields
                                model
                                {:content-type-ids (map :id content-types)})]
      (cond->> (add-fields content-types content-type-fields)
        count-items (add-content-item-counts model))))

  (get-content-items
    [model
     {:keys [content-type-ids
             content-type-slugs
             content-item-ids
             content-item-slugs]
      :as _opts}]
    (let [content-types-opts (cond-> {}
                               content-type-ids (assoc :content-type-ids content-type-ids)
                               content-type-slugs (assoc :content-type-slugs content-type-slugs))
          content-types (content-types/get-content-types model content-types-opts)
          content-types-by-id (medley/index-by :id content-types)
          fields-index (->> content-types
                            (mapcat :fields)
                            (medley/index-by :id))
          result (when (seq content-types-by-id)
                   (let [constraints (cond-> [[:in :content-type-id (keys content-types-by-id)]]
                                       (seq content-item-ids) (conj [:in :id content-item-ids]))]
                     (->> (db/execute! ds {:select [:*]
                                           :from [content-items-table]
                                           :where (sqlite/sql-and constraints)})
                          (map row->content-item)
                          (map (fn [{:keys [document] :as content-item}]
                                 (let [fields (update-keys document #(get-in fields-index [% :name]))]
                                   (assoc content-item :fields fields))))
                          (map #(assoc % :content-type (get content-types-by-id (:content-type-id %))))
                          (map #(dissoc % :content-type-id)))))]
      (cond->> result
        (seq content-item-slugs)
        (filter #(contains? (set content-item-slugs)
                            (get-in % [:fields "Slug"]))))))

  (create-content-type! [_ content-type]
    (let [{:keys [fields]} content-type
          content-type-insert {:insert-into content-types-table
                               :values [(content-type->row content-type)]
                               :on-conflict :id
                               :do-nothing true}
          field-inserts (->> fields
                             (map (fn [field]
                                    {:insert-into fields-table
                                     :values [(field->row field)]
                                     :on-conflict :id
                                     :do-nothing true})))
          content-type-field-inserts (->> fields
                                          (map (fn [field]
                                                 (let [content-type-field (->content-type-field
                                                                            {:content-type content-type
                                                                             :field field
                                                                             :rank (:rank field)
                                                                             :current-user current-user})]
                                                   {:insert-into content-type-fields-table
                                                    :values [(content-type-field->row content-type-field)]
                                                    :on-conflict :id
                                                    :do-nothing true}))))
          all-sql (concat [content-type-insert]
                          field-inserts
                          content-type-field-inserts)]
      (doseq [sql all-sql]
        (db/execute-one! ds sql))))

  (update-content-type! [_ content-type]
    (let [{:keys [fields]} content-type
          content-type-insert {:insert-into content-types-table
                               :values [(content-type->row content-type)]
                               :on-conflict :id
                               :do-update-set [:name :slug]}
          field-inserts (when fields
                          (->> fields
                               (map (fn [field]
                                      {:insert-into fields-table
                                       :values [(field->row field)]
                                       :on-conflict :id
                                       :do-update-set [:name :type]}))))
          content-type-field-deletes (when fields
                                       [{:delete-from content-type-fields-table
                                         :where [:= :content-type-id (:id content-type)]}])
          content-type-field-inserts (when fields
                                       (->> fields
                                            (map (fn [field]
                                                   (let [content-type-field (->content-type-field
                                                                              {:content-type content-type
                                                                               :field field
                                                                               :rank (:rank field)
                                                                               :current-user current-user})]
                                                     {:insert-into content-type-fields-table
                                                      :values [(content-type-field->row content-type-field)]})))))
          all-sql (concat [content-type-insert]
                          field-inserts
                          content-type-field-deletes
                          content-type-field-inserts)]
      (doseq [sql all-sql]
        (db/execute-one! ds sql))))

  (delete-content-type! [_ content-type]
    (db/execute-one! ds {:delete-from content-type-fields-table
                         :where [:= :content-type-id (:id content-type)]})
    (db/execute-one! ds {:delete-from content-types-table
                         :where [:= :id (:id content-type)]}))

  (create-content-item! [_ content-item]
    (db/execute-one! ds {:insert-into content-items-table
                         :values [(content-item->row content-item)]}))

  (delete-content-item! [_ content-item]
    (db/execute-one! ds {:delete-from content-items-table
                         :where [:= :id (:id content-item)]}))

  (update-content-item! [_ content-item]
    (db/execute-one! ds {:insert-into content-items-table
                         :values [(content-item->row content-item)]
                         :on-conflict :id
                         :do-update-set [:document #_:updated-at #_:updated-by]})))

(defn ->model [opts]
  (let [defaults {:fields-table :fields
                  :content-types-table :content-types
                  :content-type-fields-table :content-type-fields
                  :content-items-table :content-items
                  :migration-events-table :content-types-migration-events}
        no-default [:ds :current-user]
        opts' (merge defaults (select-keys opts (into no-default (keys defaults))))]
    (map->Model opts')))

(defmethod content-types/->model :sqlite [opts]
  (->model opts))

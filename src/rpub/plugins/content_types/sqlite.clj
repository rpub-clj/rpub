(ns rpub.plugins.content-types.sqlite
  {:no-doc true}
  (:require [babashka.json :as json]
            [clojure.set :as set]
            [medley.core :as medley]
            [rads.migrate :as migrate]
            [rpub.lib.db :as db]
            [rpub.model :as model]
            [rpub.model.content-types :as ct-model]
            [rpub.model.sqlite :as sqlite]
            [rpub.plugins.content-types.sqlite.migrations :as migrations]))

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
      (dissoc :content-type)
      (select-keys [:id :content-type-id :document :created-at :created-by
                    :updated-at :updated-by])))

(defn- field->row [field]
  (-> field
      (update :type name)
      (dissoc :rank)
      (select-keys [:id :app-id :name :type :created-at :created-by
                    :updated-at :updated-by])))

(defn ->content-type-field [{:keys [current-user content-type field rank] :as opts}]
  (as-> {:id (or (:id opts) (random-uuid))
         :content-type-id (:id content-type)
         :field-id (:id field)
         :rank rank} $
    (model/add-metadata $ current-user)))

(defn content-type-field->row [content-type-field]
  (select-keys content-type-field [:id :content-type-id :field-id :rank
                                   :created-at :created-by :updated-at
                                   :updated-by]))

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

(defrecord Model
           [ds
            app-id
            current-user
            fields-table
            content-types-table
            content-type-fields-table
            content-items-table
            migration-events-table]
  ct-model/Model
  (migrate! [model]
    (migrate/migrate! (migrations/config model)))

  (get-content-types
    [model
     {:keys [content-type-ids
             content-type-slugs
             count-items]}]
    (let [or-constraints (cond-> []
                           content-type-ids (conj [:in :id content-type-ids])
                           content-type-slugs (conj [:in :slug (map name content-type-slugs)]))
          sql (cond-> {:select [:id :name :slug :created-at :created-by :updated-at :updated-by]
                       :from content-types-table
                       :where [:= :app-id app-id]}
                (seq or-constraints)
                (update :where (fn [x] [:and x (db/sql-or or-constraints)])))
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
          content-types (ct-model/get-content-types model content-types-opts)
          content-types-by-id (medley/index-by :id content-types)
          fields-index (->> content-types
                            (mapcat :fields)
                            (medley/index-by :id))
          result (when (seq content-types-by-id)
                   (let [constraints (cond-> [[:in :content-type-id (keys content-types-by-id)]]
                                       (seq content-item-ids) (conj [:in :id content-item-ids]))]
                     (->> (db/execute! ds {:select [:*]
                                           :from [content-items-table]
                                           :where (db/sql-and constraints)})
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
        no-default [:ds :app-id :current-user]
        opts' (merge defaults (select-keys opts (into no-default (keys defaults))))]
    (map->Model opts')))

(defmethod ct-model/->model :sqlite [opts]
  (->model opts))

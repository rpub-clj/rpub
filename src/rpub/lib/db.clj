(ns rpub.lib.db
  {:no-doc true}
  (:require [clj-uuid :as uuid]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]))

(defn db-type [database-url]
  (let [[_ v] (re-matches #"jdbc:([^:]+):.+" database-url)]
    (keyword v)))

(defn get-datasource [database-url]
  (-> (jdbc/get-datasource database-url)
      (jdbc/with-options jdbc/unqualified-snake-kebab-opts)))

(defn uuid-column [column-name & constraints]
  (let [exact-length [:= [:length column-name] 36]
        uuid-check [:check (if (contains? (set constraints) [:not nil])
                             exact-length
                             [:or [:= column-name nil] exact-length])]]
    (into [column-name :text uuid-check] constraints)))

(defn boolean-column [column-name & constraints]
  (let [boolean-check [:check (if (contains? (set constraints) [:not nil])
                                [:in column-name [0 1]]
                                [:or
                                 [:in column-name [0 1]]
                                 [:= column-name nil]])]]
    (into [column-name :integer boolean-check] constraints)))

(defn references [column-vec ref-table-name ref-column-name]
  (into column-vec [[:references ref-table-name ref-column-name]
                    [:raw "DEFERRABLE INITIALLY DEFERRED"]]))

(defn execute! [ds sql]
  (let [stmt (cond-> sql (map? sql) sql/format)]
    (jdbc/execute! ds stmt)))

(defn execute-one! [ds sql]
  (let [stmt (cond-> sql (map? sql) sql/format)]
    (jdbc/execute-one! ds stmt)))

(defn strict [sql-map]
  (-> (sql/format sql-map)
      (update 0 #(str % " STRICT"))))

(defn wrap-db-transaction [handler]
  (fn [{:keys [model] :as req}]
    (jdbc/with-transaction+options [tx (:ds model)]
      (let [tx' (with-meta tx {:transaction-id (uuid/v6)})
            req' (-> req
                     (assoc :conn (:ds model))
                     (update-vals #(if (:ds %) (assoc % :ds tx') %)))]
        (handler req')))))

(defn int->bool [n]
  (case n
    0 false
    1 true))

(defn index-name [table suffix]
  (keyword (str (name table) "-" (name suffix) "-idx")))

(def audit-columns
  [[:created-at :text [:not nil]]
   [:created-by :text [:not nil]]
   [:updated-at :text]
   [:updated-by :text]])

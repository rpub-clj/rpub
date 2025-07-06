(ns rpub.model.common
  (:import (java.time Instant)))

(defn add-metadata [{:keys [created-at created-by app-id] :as entity} current-user]
  (cond-> entity
    (not app-id) (assoc :app-id (:app-id current-user))
    created-at (assoc :updated-at (Instant/now))
    created-by (assoc :updated-by (:id current-user))
    (not created-at) (assoc :created-at (Instant/now))
    (not created-by) (assoc :created-by (:id current-user))))

(defn row->metadata [row]
  (cond-> row
    (:id row) (update :id parse-uuid)
    (:app-id row) (update :app-id parse-uuid)
    (:user-id row) (update :user-id parse-uuid)
    (:created-at row) (update :created-at #(Instant/parse %))
    (:created-by row) (update :created-by parse-uuid)
    (:updated-at row) (update :updated-at #(Instant/parse %))
    (:updated-by row) (update :updated-by parse-uuid)))

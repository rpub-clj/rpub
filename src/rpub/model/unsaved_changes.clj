(ns rpub.model.unsaved-changes
  (:require [rpub.model.common :as common]))

^:clj-reload/keep
(defprotocol Model
  (-get-unsaved-changes [model opts])
  (-update-unsaved-changes! [model unsaved-changes])
  (-delete-unsaved-changes! [model opts]))

(defn- coerce [model]
  (get-in model [:models :unsaved-changes] model))

(defn get-unsaved-changes [model opts]
  (-get-unsaved-changes (coerce model) opts))

(defn update-unsaved-changes! [model opts]
  (-update-unsaved-changes! (coerce model) opts))

(defn delete-unsaved-changes! [model opts]
  (-delete-unsaved-changes! (coerce model) opts))

(defn ->unsaved-changes
  [{:keys [id key client-id value created-at created-by current-user]
    :as _opts}]
  (-> {:id (or id (random-uuid))
       :user-id (:id current-user)
       :client-id client-id
       :key key
       :value value
       :created-at created-at
       :created-by created-by}
      (common/add-metadata current-user)))

(defmulti ->model :db-type)

(ns rpub.model.users
  {:no-doc true}
  (:require [buddy.hashers :as hashers]
            [rpub.model.common :as common]))

^:clj-reload/keep
(defprotocol Model
  (-get-users [model opts])
  (-create-user! [model user])
  (-get-roles [model opts])
  (-create-role! [model role])
  (-get-user-roles [model opts])
  (-create-user-role! [model user-role]))

(defn- coerce [model]
  (get-in model [:models :users] model))

(defn get-users [model opts]
  (-get-users (coerce model) opts))

(defn create-user! [model opts]
  (-create-user! (coerce model) opts))

(defn get-roles [model opts]
  (-get-roles (coerce model) opts))

(defn create-role! [model opts]
  (-create-role! (coerce model) opts))

(defn get-user-roles [model opts]
  (-get-user-roles (coerce model) opts))

(defn create-user-role! [model opts]
  (-create-user-role! (coerce model) opts))

(defn ->user
  [& {:keys [id username password password-hash current-user]
      :as _opts}]
  (cond-> (-> {:id (or id (random-uuid))
               :username username}
              (common/add-metadata current-user))
    password-hash (assoc :password-hash password-hash)
    (not password-hash) (assoc :password-hash (hashers/derive password))))

(defn verify-password [user attempt]
  (:valid (hashers/verify attempt (:password-hash user))))

(defn get-current-user [{:keys [model] :as req}]
  (let [user-id (get-in req [:identity :id])]
    (first (get-users model {:ids [user-id], :roles true}))))

(def system-user
  {:id #uuid"00000000-0000-0000-0000-000000000000"})

(defn ->role [& {:keys [id label permissions current-user] :as _opts}]
  (-> {:id (or id (random-uuid))
       :label label
       :permissions permissions}
      (common/add-metadata current-user)))

(defmulti ->model :db-type)

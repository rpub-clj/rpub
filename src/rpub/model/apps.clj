(ns rpub.model.apps
  {:no-doc true}
  (:require [rpub.model.common :as common]))

^:clj-reload/keep
(defprotocol Model
  (-get-apps [model opts])
  (-create-app! [model app]))

(defn- coerce [model]
  (get-in model [:models :apps] model))

(defn get-apps [model opts]
  (-get-apps (coerce model) opts))

(defn create-app! [model opts]
  (-create-app! (coerce model) opts))

(defn ->app [& {:keys [id domains new-user current-user] :as _opts}]
  (-> {:id (or id (random-uuid))
       :domains domains
       :new-user new-user}
      (common/add-metadata current-user)))

(defmulti ->model :db-type)

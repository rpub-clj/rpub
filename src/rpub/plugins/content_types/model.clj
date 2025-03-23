(ns rpub.plugins.content-types.model
  {:no-doc true}
  (:require [rpub.model :as model]))

(def slug-field-id #uuid"6bd0ff7a-b720-4972-b98a-2aa85d179357")
(def title-field-id #uuid"cd334826-1ec6-4906-8e7f-16ece1865faf")
(def content-field-id #uuid"65a6aa2e-73a3-4283-afe1-58e610d6727d")

^:clj-reload/keep
(defprotocol Model
  (migrate! [model])
  (get-content-types [model opts])
  (get-content-items [model opts])
  (create-content-type! [model opts])
  (update-content-type! [model opts])
  (delete-content-type! [model opts])
  (create-content-item! [model opts])
  (update-content-item! [model opts])
  (delete-content-item! [model opts]))

(defmulti ->model :db-type)

(defn ->field [{:keys [current-user name type rank] :as opts}]
  (as-> {:id (or (:id opts) (random-uuid))
         :name name
         :type type
         :rank rank} $
    (model/add-metadata $ current-user)))

(defn ->content-type [{:keys [current-user name slug fields] :as opts}]
  (let [fields (map #(->field (assoc % :current-user current-user)) fields)]
    (as-> {:id (or (:id opts) (random-uuid))
           :name name
           :slug slug
           :fields fields} $
      (model/add-metadata $ current-user))))

(defn ->content-item [{:keys [current-user content-type document] :as opts}]
  (as-> {:id (or (:id opts) (random-uuid))
         :document document
         :content-type content-type} $
    (model/add-metadata $ current-user)))

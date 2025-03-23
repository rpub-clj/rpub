(ns rpub.plugins.content-types.rpc
  {:no-doc true}
  (:require [medley.core :as medley]
            [ring.util.response :as response]
            [rpub.api.impl :as api-impl]
            [rpub.model :as model]
            [rpub.plugins.content-types.model :as ct-model]
            [rpug.plugins.content-types :as-alias ct])
  (:import (java.time Instant)))

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

(def slug-field-id #uuid"6bd0ff7a-b720-4972-b98a-2aa85d179357")

(defn new-content-type-field [{:keys [::ct/model body-params current-user] :as _req}]
  (let [{:keys [content-type-id type rank]} body-params
        [content-type] (ct-model/get-content-types model {:content-type-ids [content-type-id]})
        new-field (->field {:name "New Field"
                            :type (or type :text)
                            :rank (or rank (apply max 0 (map :rank (:fields content-type))))
                            :current-user current-user})
        content-type' (update content-type :fields conj new-field)]
    (ct-model/update-content-type! model content-type')
    (response/response {:success true})))

(defn update-content-type-field [{:keys [::ct/model body-params] :as _req}]
  (let [{:keys [content-type-id content-field-id]} body-params
        [content-type] (ct-model/get-content-types model {:content-type-ids [content-type-id]})
        updated-field (-> (medley/find-first #(= (:id %) content-field-id)
                                             (:fields content-type))
                          (merge (select-keys body-params [:name :type :rank])))
        content-type' (update content-type :fields
                              (fn [fields]
                                (map (fn [field]
                                       (if (= (:id field) content-field-id)
                                         updated-field
                                         field))
                                     fields)))]
    (ct-model/update-content-type! model content-type')
    (response/response {:success true})))

(defn delete-content-type-field [{:keys [::ct/model body-params] :as _req}]
  (let [body-params' (update-vals body-params parse-uuid)
        {:keys [content-type-id content-field-id]} body-params'
        [content-type] (ct-model/get-content-types model {:content-type-ids [content-type-id]})
        content-type' (update content-type :fields
                              (fn [fields]
                                (remove #(= (:id %) content-field-id)
                                        fields)))]
    (ct-model/update-content-type! model content-type')
    (response/response {:success true})))

(defn delete-content-type [{:keys [::ct/model body-params] :as _req}]
  (let [{:keys [content-type-id]} body-params]
    (ct-model/delete-content-type! model {:id content-type-id})
    (response/response {:success true})))

(defn new-content-type [{:keys [::ct/model current-user] :as _req}]
  (let [content-type (->content-type {:name "New Content Type"
                                      :slug "new-content-type"
                                      :fields []
                                      :current-user current-user})]
    (ct-model/create-content-type! model content-type)
    (response/response {:content-type-id (:id content-type)})))

(defn update-content-type [{:keys [::ct/model body-params] :as _req}]
  (let [params-content-type (get body-params :content-type)
        [existing-content-type] (ct-model/get-content-types model {:content-type-ids [(:id params-content-type)]})
        updated-content-type (merge existing-content-type params-content-type)]
    (ct-model/update-content-type! model updated-content-type)
    (response/response {:success true})))

(defn new-content-item [{:keys [::ct/model body-params current-user] :as _req}]
  (let [{:keys [content-type-id document]} body-params
        [content-type] (ct-model/get-content-types
                         model
                         {:content-type-ids [content-type-id]})
        content-item (->content-item {:content-type content-type
                                      :document document
                                      :current-user current-user})]
    (ct-model/create-content-item! model content-item)
    (response/response {:content-item-id (:id content-item)
                        :content-item-slug (get-in content-item [:document slug-field-id])})))

(defn edit-content-item-api [{:keys [::ct/model body-params current-user] :as _req}]
  (let [{:keys [content-item-id document]} body-params
        [content-item] (ct-model/get-content-items
                         model
                         {:content-item-ids [content-item-id]})
        content-item' (-> content-item
                          (merge {:document document
                                  :updated-by (:id current-user)
                                  :updated-at (Instant/now)})
                          (select-keys [:id :document :created-at
                                        :created-by :updated-at
                                        :updated-by]))]
    (ct-model/update-content-item! model content-item')
    (response/response {:success true})))

(defn delete-content-item-api [{:keys [::ct/model body-params] :as _req}]
  (let [{:keys [content-item-id]} body-params
        [content-item] (ct-model/get-content-items
                         model
                         {:content-item-ids [content-item-id]})]
    (ct-model/delete-content-item! model content-item)
    (response/response {:success true})))

(defn routes [opts]
  ["/api" {:middleware (api-impl/api-middleware opts)}
   ["/new-content-type" {:post new-content-type}]
   ["/update-content-type" {:post update-content-type}]
   ["/delete-content-type" {:post delete-content-type}]
   ["/new-content-type-field" {:post new-content-type-field}]
   ["/new-content-item" {:post new-content-item}]
   ["/edit-content-item" {:post edit-content-item-api}]
   ["/delete-content-item" {:post delete-content-item-api}]
   ["/update-content-type-field" {:post update-content-type-field}]
   ["/delete-content-type-field" {:post delete-content-type-field}]])

(ns rpub.plugins.content-types
  {:no-doc true}
  (:require [clojure.set :as set]
            [medley.core :as medley]
            [rads.inflections :as inflections]
            [ring.util.response :as response]
            [rpub.admin.impl :as admin-impl]
            [rpub.api.impl :as api-impl]
            [rpub.lib.html :as html]
            [rpub.model :as model])
  (:import (java.time Instant)))

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

(defn all-content-types-handler [req]
  (let [content-types (->> (get-content-types (::model req) {:count-items true})
                           (sort-by :name))]
    (admin-impl/page-response
      req
      {:title "Content Types"
       :primary
       (html/cljs
         [:all-content-types-page {:content-types content-types}]
         {:format :transit})})))

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

(defn- admin-menu-items [model]
  {:content-types (->> (get-content-types model {})
                       (map (fn [c]
                              {:id (:id c)
                               :label (:name c)
                               :href (str "/admin/content-types/" (name (:slug c)))})))
   :plugins [{:label "Content Types"
              :href "/admin/content-types"}]})

(defn wrap-content-types [handler]
  (fn [{:keys [db-type current-user] :as req}]
    (let [ds (get-in req [:model :ds])
          model (->model {:db-type db-type
                          :ds ds
                          :current-user current-user})
          req' (-> req
                   (update :model assoc :content-types-model model)
                   (assoc ::model model)
                   (update :admin-menu-items conj (admin-menu-items model)))]
      (handler req'))))

(defn single-content-type-page [{:keys [model path-params] :as req}]
  (let [{:keys [content-type-slug]} path-params
        [content-type] (get-content-types (::model req) {:content-type-slugs [content-type-slug]})
        content-items (get-content-items (::model req) {:content-type-ids [(:id content-type)]})
        users-index (->> (model/get-users model {:ids (map :created-by content-items)})
                         (medley/index-by :id))
        content-items' (map (fn [content-item]
                              (let [user (get users-index (:created-by content-item))]
                                (assoc content-item :created-by user)))
                            content-items)]
    (admin-impl/page-response
      req
      {:title (:name content-type)
       :primary
       (html/cljs
         [:single-content-type-page {:content-type content-type
                                     :content-items content-items'}]
         {:format :json})})))

(defn new-content-item-page [{:keys [::model path-params site-base-url settings] :as req}]
  (let [{:keys [content-type-slug]} path-params
        [content-type] (get-content-types model {:content-type-slugs [content-type-slug]})
        permalink-single (get-in settings [:permalink-single :value])]
    (admin-impl/page-response
      req
      {:title (str "New " (inflections/singular (:name content-type)))
       :primary
       (html/cljs
         [:new-content-item-page
          {:content-type content-type
           :permalink-single permalink-single
           :site-base-url site-base-url}]
         {:format :json})})))

(defn md->html [s]
  #_(-> (md/md-to-html-string
          s
          :heading-anchors true)))

(def slug-field-id #uuid"6bd0ff7a-b720-4972-b98a-2aa85d179357")
(def title-field-id #uuid"cd334826-1ec6-4906-8e7f-16ece1865faf")
(def content-field-id #uuid"65a6aa2e-73a3-4283-afe1-58e610d6727d")
(def url-field-id #uuid"e37838ee-d16b-4a8d-87fe-d4f5042a04ed")

(defn edit-content-item-page [{:keys [::model path-params site-base-url settings] :as req}]
  (let [{:keys [content-item-slug]} path-params
        [content-item] (get-content-items
                         model
                         {:content-item-slugs [content-item-slug]})
        content-item' (-> content-item
                          (dissoc :fields)
                          #_(update-in [:document content-field-id] md->html))
        {:keys [content-type]} content-item'
        permalink-single (get-in settings [:permalink-single :value])]
    (admin-impl/page-response
      req
      {:title (str "Edit " (inflections/singular (:name content-type)))
       :primary
       (html/cljs
         [:edit-content-item-page
          {:content-item content-item'
           :content-type content-type
           :permalink-single permalink-single
           :site-base-url site-base-url}]
         {:format :json})})))

(defn str->field-type [s]
  (keyword s))

(defn param-values [form-params k]
  (let [v (get form-params k)]
    (if (coll? v) v [v])))

(defn update-content-types [{:keys [form-params current-user] :as req}]
  (let [content-type-id (parse-uuid (get form-params "content-type-id"))
        content-type-name (get form-params "content-type-name")
        field-ids (when-let [ids (param-values form-params "field-id")]
                    (map parse-uuid ids))
        field-names (->> (zipmap field-ids (param-values form-params "field-name"))
                         (map (fn [[id name]] {:id id, :name name})))
        field-ranks (->> (zipmap field-ids (param-values form-params "field-rank"))
                         (map (fn [[id rank]] {:id id, :rank (Integer/parseInt rank)})))
        field-types (->> (zipmap
                           field-ids
                           (map str->field-type (param-values form-params "field-type")))
                         (map (fn [[id type]] {:id id, :type type})))
        fields (map #(->field (assoc % :current-user current-user))
                    (-> field-names
                        (set/join field-types {:id :id})
                        (set/join field-ranks {:id :id})))
        [content-type] (get-content-types
                         (::model req)
                         {:content-type-ids [content-type-id]})
        content-type' (if content-type
                        (assoc content-type :fields fields)
                        (->content-type {:name content-type-name
                                         :slug (model/->slug content-type-name)
                                         :fields fields
                                         :current-user current-user}))]
    (update-content-type! (::model req) content-type')
    (all-content-types-handler req)))

(defn new-content-type-field [{:keys [::model body-params current-user] :as _req}]
  (let [{:keys [content-type-id type rank]} body-params
        [content-type] (get-content-types model {:content-type-ids [content-type-id]})
        new-field (->field {:name "New Field"
                            :type (or type :text)
                            :rank (or rank (apply max 0 (map :rank (:fields content-type))))
                            :current-user current-user})
        content-type' (update content-type :fields conj new-field)]
    (update-content-type! model content-type')
    (response/response {:success true})))

(defn update-content-type-field [{:keys [::model body-params] :as _req}]
  (let [{:keys [content-type-id content-field-id]} body-params
        [content-type] (get-content-types model {:content-type-ids [content-type-id]})
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
    (update-content-type! model content-type')
    (response/response {:success true})))

(defn delete-content-type-field [{:keys [::model body-params] :as _req}]
  (let [body-params' (update-vals body-params parse-uuid)
        {:keys [content-type-id content-field-id]} body-params'
        [content-type] (get-content-types model {:content-type-ids [content-type-id]})
        content-type' (update content-type :fields
                              (fn [fields]
                                (remove #(= (:id %) content-field-id)
                                        fields)))]
    (update-content-type! model content-type')
    (response/response {:success true})))

(defn delete-content-type [{:keys [::model body-params] :as _req}]
  (let [{:keys [content-type-id]} body-params]
    (delete-content-type! model {:id content-type-id})
    (response/response {:success true})))

(defn new-content-type [{:keys [::model current-user] :as _req}]
  (let [content-type (->content-type {:name "New Content Type"
                                      :slug "new-content-type"
                                      :fields []
                                      :current-user current-user})]
    (create-content-type! model content-type)
    (response/response {:content-type-id (:id content-type)})))

(defn update-content-type [{:keys [::model body-params] :as _req}]
  (let [params-content-type (get body-params :content-type)
        [existing-content-type] (get-content-types model {:content-type-ids [(:id params-content-type)]})
        updated-content-type (merge existing-content-type params-content-type)]
    (update-content-type! model updated-content-type)
    (response/response {:success true})))

(defn new-content-item [{:keys [::model body-params current-user] :as req}]
  (let [{:keys [content-type-id document]} body-params
        [content-type] (get-content-types
                         model
                         {:content-type-ids [content-type-id]})
        content-item (->content-item {:content-type content-type
                                      :document document
                                      :current-user current-user})]
    (create-content-item! model content-item)
    (response/response {:content-item-id (:id content-item)
                        :content-item-slug (get-in content-item [:document slug-field-id])})))

(defn edit-content-item [{:keys [::model body-params current-user] :as req}]
  (let [{:keys [content-item-id document]} body-params
        [content-item] (get-content-items
                         model
                         {:content-item-ids [content-item-id]})
        content-item' (-> content-item
                          (merge {:document document
                                  :updated-by (:id current-user)
                                  :updated-at (Instant/now)})
                          (select-keys [:id :document :created-at
                                        :created-by :updated-at
                                        :updated-by]))]
    (update-content-item! model content-item')
    (response/response {:success true})))

(defn delete-content-item [{:keys [::model body-params] :as req}]
  (let [{:keys [content-item-id]} body-params
        [content-item] (get-content-items
                         model
                         {:content-item-ids [content-item-id]})]
    (delete-content-item! model content-item)
    (response/response {:success true})))

(defn routes [opts]
  [["/api" {:middleware (api-impl/api-middleware opts)}
    ["/new-content-type" {:post new-content-type}]
    ["/update-content-type" {:post update-content-type}]
    ["/delete-content-type" {:post delete-content-type}]
    ["/new-content-type-field" {:post new-content-type-field}]
    ["/new-content-item" {:post new-content-item}]
    ["/edit-content-item" {:post edit-content-item}]
    ["/delete-content-item" {:post delete-content-item}]
    ["/update-content-type-field" {:post update-content-type-field}]
    ["/delete-content-type-field" {:post delete-content-type-field}]]
   ["/admin/content-types" {:middleware (admin-impl/admin-middleware opts)}
    [["" {:get all-content-types-handler
          :post update-content-types}]
     ["/{content-type-slug}" {:get single-content-type-page}]
     ["/{content-type-slug}/new" {:get new-content-item-page}]
     ["/{content-type-slug}/{content-item-slug}" {:get edit-content-item-page}]]]])

(defn init [{:keys [model current-user] :as _opts}]
  (let [model (->model (merge (model/db-info model)
                              {:current-user current-user}))]
    (migrate! model)))

(defn middleware [_]
  [wrap-content-types])

(defmethod model/internal-plugin ::plugin [_]
  {:init init
   :middleware middleware
   :routes routes})

(ns rpub.plugins.content-types.admin
  {:no-doc true}
  (:require [clojure.set :as set]
            [medley.core :as medley]
            [rads.inflections :as inflections]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]
            [rpub.model :as model]
            [rpub.plugins.content-types :as-alias ct]
            [rpub.plugins.content-types.model :as ct-model]))

(defn all-content-types-handler [{:keys [::ct/field-types] :as req}]
  (let [content-types (->> (ct-model/get-content-types (::ct/model req) {:count-items true})
                           (sort-by :name))]
    (admin-impl/page-response
      req
      {:title "Content Types"
       :primary
       (html/custom-element
         [:all-content-types-page
          {:content-types content-types
           :field-types field-types}]
         {:format :transit})})))

(defn single-content-type-handler [{:keys [model path-params] :as req}]
  (let [{:keys [content-type-slug]} path-params
        [content-type] (ct-model/get-content-types (::ct/model req) {:content-type-slugs [content-type-slug]})
        content-items (ct-model/get-content-items (::ct/model req) {:content-type-ids [(:id content-type)]})
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
       (html/custom-element
         [:single-content-type-page {:content-type content-type
                                     :content-items content-items'}]
         {:format :transit})})))

(defn new-content-item-handler [{:keys [::ct/model ::ct/field-types path-params site-base-url settings] :as req}]
  (let [{:keys [content-type-slug]} path-params
        [content-type] (ct-model/get-content-types model {:content-type-slugs [content-type-slug]})
        permalink-single (get-in settings [:permalink-single :value])]
    (admin-impl/page-response
      req
      {:title (str "New " (inflections/singular (:name content-type)))
       :primary
       (html/custom-element
         [:new-content-item-page
          {:content-type content-type
           :field-types field-types
           :permalink-single permalink-single
           :site-base-url site-base-url}]
         {:format :transit})})))

(defn md->html [_s]
  #_(-> (md/md-to-html-string
          s
          :heading-anchors true)))

(def content-field-id #uuid"65a6aa2e-73a3-4283-afe1-58e610d6727d")

(defn edit-content-item-handler [{:keys [::ct/model ::ct/field-types path-params site-base-url settings] :as req}]
  (let [{:keys [content-item-slug]} path-params
        [content-item] (ct-model/get-content-items
                         model
                         {:content-item-slugs [content-item-slug]})
        content-item' (-> content-item
                          #_(dissoc :fields)
                          #_(update-in [:document content-field-id] md->html))
        {:keys [content-type]} content-item'
        permalink-single (get-in settings [:permalink-single :value])]
    (admin-impl/page-response
      req
      {:title (str "Edit " (inflections/singular (:name content-type)))
       :primary
       (html/custom-element
         [:edit-content-item-page
          {:content-item content-item'
           :content-type content-type
           :field-types field-types
           :permalink-single permalink-single
           :site-base-url site-base-url}]
         {:format :transit})})))

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
        fields (map #(ct-model/->field (assoc % :current-user current-user))
                    (-> field-names
                        (set/join field-types {:id :id})
                        (set/join field-ranks {:id :id})))
        [content-type] (ct-model/get-content-types
                         (::ct/model req)
                         {:content-type-ids [content-type-id]})
        content-type' (if content-type
                        (assoc content-type :fields fields)
                        (ct-model/->content-type {:name content-type-name
                                                  :slug (model/->slug content-type-name)
                                                  :fields fields
                                                  :current-user current-user}))]
    (ct-model/update-content-type! (::ct/model req) content-type')
    (all-content-types-handler req)))

(defn routes [opts]
  ["/admin/content-types" {:middleware (admin-impl/admin-middleware opts)}
   [["" {:get all-content-types-handler
         :post update-content-types}]
    ["/{content-type-slug}" {:get single-content-type-handler}]
    ["/{content-type-slug}/content-items/new" {:get new-content-item-handler}]
    ["/{content-type-slug}/content-items/{content-item-slug}" {:get edit-content-item-handler}]]])

(ns rpub.plugins.content-types.admin
  {:no-doc true}
  (:require [medley.core :as medley]
            [rads.inflections :as inflections]
            [reitit.core :as reitit]
            [ring.util.response :as response]
            [rpub.lib.html :as html]
            [rpub.model.content-types :as ct-model]
            [rpub.model.unsaved-changes :as unsaved-changes]
            [rpub.model.users :as users]
            [rpub.plugins.admin.helpers :as admin-helpers]
            [rpub.plugins.content-types :as-alias ct]))

(defn- get-content-types [req]
  (->> (ct-model/get-content-types (::ct/model req) {:count-items true})
       (sort-by :name)))

(defn- get-unsaved-changes [{:keys [model current-user] :as _req} k]
  (unsaved-changes/get-unsaved-changes
    model
    {:user-ids [(:id current-user)]
     :keys [k]}))

(defn- last-updated [unsaved-changes]
  (->> unsaved-changes
       (sort-by #(or (:updated-at %) (:created-at %)) #(compare %2 %1))
       first))

(defn all-content-types-handler [{:keys [current-user ::ct/field-types] :as req}]
  (let [content-types (get-content-types req)
        unsaved-changes (-> (get-unsaved-changes req :all-content-types-page)
                            last-updated)]
    (admin-helpers/page-response
      req
      {:title "Content Types"
       :primary
       (html/custom-element
         [:all-content-types-page
          {:current-user (select-keys current-user [:id :roles])
           :content-types content-types
           :field-types field-types
           :unsaved-changes unsaved-changes}])})))

(defn single-content-type-handler [{:keys [model path-params] :as req}]
  (let [{:keys [content-type-slug]} path-params
        [content-type] (ct-model/get-content-types (::ct/model req) {:content-type-slugs [content-type-slug]})
        content-items (ct-model/get-content-items (::ct/model req) {:content-type-ids [(:id content-type)]})
        users-index (->> (users/get-users model {:ids (map :created-by content-items)})
                         (medley/index-by :id))
        content-items' (map (fn [content-item]
                              (let [user (get users-index (:created-by content-item))]
                                (assoc content-item :created-by user)))
                            content-items)]
    (admin-helpers/page-response
      req
      {:title (inflections/plural (:name content-type))
       :primary
       (html/custom-element
         [:single-content-type-page
          {:content-type content-type
           :content-items content-items'}])})))

(defn new-content-item-handler [{:keys [::ct/model ::ct/field-types path-params site-base-url settings] :as req}]
  (let [{:keys [content-type-slug]} path-params
        [content-type] (ct-model/get-content-types model {:content-type-slugs [content-type-slug]})
        permalink-single (get-in settings [:permalink-single :value])]
    (admin-helpers/page-response
      req
      {:title (str "New " (inflections/singular (:name content-type)))
       :primary
       (html/custom-element
         [:single-content-item-page
          {:editing false
           :content-type content-type
           :field-types field-types
           :permalink-single permalink-single
           :site-base-url site-base-url}])})))

(defn md->html [_s]
  #_(-> (md/md-to-html-string
          s
          :heading-anchors true)))

(def content-field-id #uuid"65a6aa2e-73a3-4283-afe1-58e610d6727d")

(defn edit-content-item-handler
  [{:keys [::ct/model ::ct/field-types path-params site-base-url
           permalink-router]
    :as req}]
  (let [{:keys [content-item-id]} path-params
        [content-item] (ct-model/get-content-items
                         model
                         {:content-item-ids [content-item-id]})
        content-item' (-> content-item
                          #_(dissoc :fields)
                          #_(update-in [:document content-field-id] md->html))
        {:keys [content-type]} content-item']
    (admin-helpers/page-response
      req
      {:title (str "Edit " (inflections/singular (:name content-type)))
       :primary
       (html/custom-element
         [:single-content-item-page
          {:editing true
           :content-item content-item'
           :content-type content-type
           :field-types field-types
           :permalink-routes (reitit/routes permalink-router)
           :site-base-url site-base-url}])})))

(defn single-content-item-handler [{:keys [path-params] :as req}]
  (let [{:keys [content-item-id]} path-params]
    (if (= content-item-id "new")
      (new-content-item-handler req)
      (edit-content-item-handler req))))

(defn- ->updated-content-types [{:keys [new-index existing-index current-user]}]
  (->> (vals new-index)
       (map #(merge (get existing-index (:id %)) %))
       (map #(assoc % :current-user current-user))
       (map #(ct-model/->content-type %))))

(defn update-content-item-handler [{:keys [body-params current-user] :as req}]
  (let [[existing-content-item] (ct-model/get-content-items
                                  (::ct/model req)
                                  {:content-item-ids [(:id body-params)]})
        updated-content-item (ct-model/->content-item
                               (merge existing-content-item
                                      (select-keys body-params [:content-type :document])
                                      {:current-user current-user}))]
    (ct-model/update-content-item! (::ct/model req) updated-content-item)
    (response/response (select-keys updated-content-item [:id]))))

(defn update-content-types-handler [{:keys [model current-user body-params] :as req}]
  (let [new-index (->> (:content-types body-params)
                       (medley/index-by :id))
        existing-index (->> (ct-model/get-content-types
                              (::ct/model req)
                              {:content-type-ids (keys new-index)})
                            (medley/index-by :id))
        updated-content-types (->updated-content-types
                                {:new-index new-index
                                 :existing-index existing-index
                                 :current-user current-user})]
    (run! #(ct-model/update-content-type! (::ct/model req) %)
          updated-content-types)
    (unsaved-changes/delete-unsaved-changes!
      model
      {:user-ids [(:id current-user)]
       :keys [:all-content-types-page]})
    (response/response {:success true})))

(defn menu-items [model]
  {:content-types (->> (ct-model/get-content-types model {})
                       (map (fn [c]
                              {:id (:id c)
                               :label (inflections/plural (:name c))
                               :href (str "/admin/content-types/" (name (:slug c)))}))
                       (sort-by :label))
   :plugins [{:label "Content Types"
              :href "/admin/content-types"}]})

(defn routes [opts]
  [["" {:middleware (admin-helpers/admin-middleware opts)}
    ["/admin/api/content-types/update-content-types"
     {:post #'update-content-types-handler}]
    ["/admin/api/content-types/update-content-item"
     {:post #'update-content-item-handler}]
    ["/admin/content-types"
     {:get #'all-content-types-handler}]
    ["/admin/content-types/{content-type-slug}"
     {:get #'single-content-type-handler}]
    ["/admin/content-types/{content-type-slug}/content-items/{content-item-id}"
     {:get #'single-content-item-handler}]]])

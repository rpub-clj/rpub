(ns rpub.plugins.content-types
  {:no-doc true}
  (:require [rpub.model :as model]
            [rpub.plugins.content-types.admin :as ct-admin]
            [rpub.plugins.content-types.model :as ct-model]
            [rpub.plugins.content-types.rest :as ct-rest]
            [rpub.plugins.content-types.rpc :as ct-rpc]))

(def get-content-types ct-model/get-content-types)
(def get-content-items ct-model/get-content-items)
(def create-content-item! ct-model/create-content-item!)
(def update-content-item! ct-model/update-content-item!)
(def delete-content-item! ct-model/delete-content-item!)

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
  {:content-types (->> (ct-model/get-content-types model {})
                       (map (fn [c]
                              {:id (:id c)
                               :label (:name c)
                               :href (str "/admin/content-types/" (name (:slug c)))})))
   :plugins [{:label "Content Types"
              :href "/admin/content-types"}]})

(def default-field-types
  {:text {:input :rpub-field-types-text
          :label "Text"
          :description "Ask for text with optional formatting."}
   :text-lg {:input :rpub-field-types-text-lg
             :label "Text (Large)"
             :description "Ask for text with optional formatting."}
   :number {:input :rpub-field-types-number
            :label "Number"
            :description "Ask for a whole number or a decimal."}
   :datetime {:input :rpub-field-types-datetime
              :label "Date and Time"
              :description "Ask for a date and time with a date picker."}
   :media {:input :rpub-field-types-media
           :label "Media"
           :description "Ask for an image or video."}
   :choice {:input :rpub-field-types-choice
            :label "Choice"
            :description "Ask for a choice between multiple options."}
   :group {:input :rpub-field-types-group
           :label "Group"
           :description "Combine multiple fields into a group."}})

(defn wrap-content-types [handler]
  (fn [{:keys [db-type current-user] :as req}]
    (let [ds (get-in req [:model :ds])
          model (ct-model/->model {:db-type db-type
                                   :ds ds
                                   :current-user current-user})
          req' (-> req
                   (update :model assoc :content-types-model model)
                   (assoc ::model model)
                   (update ::field-types #(or % default-field-types))
                   (update :admin-menu-items conj (admin-menu-items model)))]
      (handler req'))))

(def slug-field-id #uuid"6bd0ff7a-b720-4972-b98a-2aa85d179357")
(def title-field-id #uuid"cd334826-1ec6-4906-8e7f-16ece1865faf")
(def content-field-id #uuid"65a6aa2e-73a3-4283-afe1-58e610d6727d")

(defn routes [opts]
  [(ct-rpc/routes opts)
   (ct-rest/routes opts)
   (ct-admin/routes opts)])

(defn init [{:keys [model current-user] :as _opts}]
  (let [db-info (model/db-info model)
        _ (case (:db-type db-info)
            :sqlite (require 'rpub.plugins.content-types.sqlite))
        model (ct-model/->model (merge db-info {:current-user current-user}))]
    (ct-model/migrate! model)))

(defn middleware [_]
  [wrap-content-types])

(defmethod model/internal-plugin ::plugin [_]
  {:init init
   :middleware middleware
   :routes routes})

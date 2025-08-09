(ns rpub.plugins.content-types.admin.single-content-item-page
  (:require [clojure.string :as str]
            [rads.inflections :as inflections]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.lib.permalinks :as permalinks]
            [rpub.lib.substrate :refer [subscribe dispatch]]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- content-item-fields
  [{:keys [content-item editing creating field-types]}]
  (let [{:keys [content-type]} content-item]
    [:div
     (for [field (sort-by :rank (:fields content-type))
           :let [v (get-in content-item [:document (:id field)])]]
       [:div.max-w-xl.mb-4 {:key (:id field)}
        [:label.font-semibold.mb-1.block {:for (:name field)}
         (:name field)]
        [:div
         (html/custom-element
           [(get-in field-types [(:type field) :input])
            {:field field
             :value v
             :on-change (fn [e]
                          (let [v (-> e .-target .-value)]
                            (dispatch [::field-change (assoc field :value v)])))
             :editing editing
             :creating creating}])]])]))

(defn submit-form [e field-values content-item]
  (.preventDefault e)
  (dispatch [::start-submit])
  (let [content-item' (update content-item :document merge field-values)]
    (http/post "/admin/api/content-types/update-content-item"
               {:body content-item'})))

(defn new-content-item-page [{:keys [content-type field-types]}]
  (let [submitting (subscribe [::submitting])
        field-values (subscribe [::field-values])
        content-item {:content-type content-type
                      :document (->> (:fields content-type)
                                     (map (fn [field] [(:id field) nil]))
                                     (into {}))}
        handle-submit (fn [e]
                        (-> (submit-form e field-values content-item)
                            (.then (fn [content-item]
                                     (set! js/window.location
                                           (str "/admin/content-types/"
                                                (name (:slug content-type))
                                                "/content-items/"
                                                (:id content-item)))))))]
    [:div.p-4
     [helpers/box
      {:class "mb-4"
       :title (str "New " (inflections/singular (get-in content-item [:content-type :name])))}]
     [:form {:on-submit handle-submit}
      [helpers/box
       {:content
        [:div
         [content-item-fields {:content-item content-item
                               :field-types field-types
                               :creating true}]
         [html/submit-button {:ready-label "Create"
                              :submit-label "Creating..."
                              :submitting submitting}]]}]]]))

(defn- edit-content-item-page
  [{:keys [content-type content-item field-types site-base-url
           permalink-routes]}]
  (let [submitting (subscribe [::submitting])
        field-values (subscribe [::field-values])
        content-type-name-singular (inflections/singular (:name content-type))
        content-item-title (get-in content-item [:fields "Title"])
        permalink-router (permalinks/router permalink-routes)
        content-item-href (str site-base-url
                               (:path (permalinks/match-by-name
                                        permalink-router
                                        :single
                                        {:content-type-slug (name (:slug content-type))
                                         :content-item-slug (get-in content-item [:fields "Slug"])})))
        handle-delete (fn [_e]
                        (js/confirm (str "Are you sure you want to delete this "
                                         (str/lower-case content-type-name-singular)
                                         "?")))
        handle-submit (fn [e]
                        (-> (submit-form e field-values content-item)
                            (.then (fn [_]
                                     (.reload js/window.location)))))]

    [:div.p-4
     [helpers/box
      {:class "mb-4"
       :title [:div.flex
               [:div.grow
                [:span.italic.text-blue-600 (str content-type-name-singular ": ")]
                content-item-title]
               [:div [html/delete-button
                      {:on-click handle-delete}
                      "Delete"]]]
       :content [:div
                 [:a.underline.text-gray-500
                  {:href content-item-href
                   :target "_blank"}
                  content-item-href]]}]
     [:form {:on-submit handle-submit}
      [helpers/box
       {:content
        [:div
         [content-item-fields {:content-item content-item
                               :field-types field-types
                               :editing true}]
         [html/submit-button {:ready-label "Save"
                              :submit-label "Saving..."
                              :submitting submitting}]]}]]]))

(defn- single-content-item-page [{:keys [editing] :as props}]
  (if editing
    [edit-content-item-page props]
    [new-content-item-page props]))

(defn field-change [db field]
  (assoc-in db [::field-values (:id field)] (:value field)))

(defn start-submit [db]
  (assoc db ::submitting true))

(def model
  {:queries {::field-values (fn [db _] (::field-values db))
             ::field-change field-change
             ::start-submit start-submit
             ::submitting (fn [db _] (::submitting db))}
   :transactions {::field-change ::field-values
                  ::start-submit ::submitting}})

(def config
  {:page-id :single-content-item-page
   :component single-content-item-page
   :model model})

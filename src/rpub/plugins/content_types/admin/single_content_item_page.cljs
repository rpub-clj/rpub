(ns rpub.plugins.content-types.admin.single-content-item-page
  (:require [clojure.string :as str]
            [rads.inflections :as inflections]
            [rpub.lib.dag.react :refer [use-dag use-dag-values]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.lib.permalinks :as permalinks]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- content-item-fields
  [{:keys [content-item editing creating field-types]}]
  (let [[_ push] (use-dag)
        {:keys [content-type]} content-item]
    [:div
     (for [field (sort-by :rank (:fields content-type))
           :let [v (get-in content-item [:document (:id field)])
                 v' (if (= v ::new-field) "" v)]]
       [:div.max-w-xl.mb-4 {:key (:id field)}
        [:label.font-semibold.mb-1.block {:for (:name field)}
         (:name field)]
        [:div
         (html/custom-element
           [(get-in field-types [(:type field) :input])
            {:field field
             :value v'
             :on-change (fn [e]
                          (let [v (-> e .-target .-value)]
                            (push ::field-change (assoc field :value v))))
             :editing editing
             :creating creating}])]])]))

(defn new-content-item-page [{:keys [content-type field-types]}]
  (let [[{:keys [::submitting]}] (use-dag [::submitting])
        content-item {:content-type content-type
                      :document (->> (:fields content-type)
                                     (map (fn [field] [(:id field) ::new-field]))
                                     (into {}))}]
    [:div.p-4
     [helpers/box
      {:class "mb-4"
       :title (str "New " (inflections/singular (get-in content-item [:content-type :name])))}]
     [helpers/box
      {:content
       [:div
        [content-item-fields {:content-item content-item
                              :field-types field-types
                              :creating true}]
        [html/submit-button {:ready-label "Create"
                             :submit-label "Creating..."
                             :submitting submitting}]]}]]))

(defn- edit-content-item-page
  [{:keys [content-type content-item field-types site-base-url
           permalink-routes]}]
  (let [[{:keys [::submitting]} push] (use-dag [::submitting])
        dag-values (use-dag-values [::field-values])
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
        submit-form (fn [e]
                      (.preventDefault e)
                      (push ::start-submit)
                      (let [{:keys [::field-values]} (dag-values)
                            content-item' (update content-item :document merge field-values)]
                        (-> (http/post "/admin/api/content-types/update-content-item"
                                       {:body content-item'})
                            (.then (fn [_] (.reload js/window.location))))))]
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
     [:form {:on-submit submit-form}
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

(def dag-config
  {:nodes {::field-values {:calc ::field-values}
           ::field-change {:push field-change}
           ::start-submit {:push start-submit}
           ::submitting {:calc ::submitting}}
   :edges [[::field-change ::field-values]
           [::start-submit ::submitting]]})

(def config
  {:page-id :single-content-item-page
   :component single-content-item-page
   :dag-config dag-config})

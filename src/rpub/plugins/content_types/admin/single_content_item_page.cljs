(ns rpub.plugins.content-types.admin.single-content-item-page
  (:require [clojure.string :as str]
            [rads.inflections :as inflections]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]
            [rpub.lib.permalinks :as permalinks]))

(defn- content-item-fields [{:keys [content-item editing creating field-types]}]
  (let [{:keys [content-type]} content-item]
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
             :on-change #(prn (-> % .-target .-value))
             :editing editing
             :creating creating}])]])]))

(defn new-content-item-page [{:keys [content-type field-types]}]
  (let [content-item {:content-type content-type
                      :document (->> (:fields content-type)
                                     (map (fn [field] [(:id field) ::new-field]))
                                     (into {}))}
        submitting false]
    [:div.p-4
     [admin-impl/box
      {:class "mb-4"
       :title (str "New " (inflections/singular (get-in content-item [:content-type :name])))}]
     [admin-impl/box
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
  (let [submitting false
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
                                         "?")))]
    [:div.p-4
     [admin-impl/box
      {:class "mb-4"
       :title [:div.flex
               [:div.grow
                [:span.italic.text-blue-600 (str content-type-name-singular ": ")]
                content-item-title]
               [:div [html/delete-button
                      {:on-click handle-delete}
                      (str "Delete " content-type-name-singular)]]]
       :content [:div
                 [:a.underline.text-gray-500
                  {:href content-item-href
                   :target "_blank"}
                  content-item-href]]}]
     [admin-impl/box
      {:content
       [:div
        [content-item-fields {:content-item content-item
                              :field-types field-types
                              :editing true}]
        [html/submit-button {:ready-label "Save"
                             :submit-label "Saving..."
                             :submitting submitting}]]}]]))

(defn- single-content-item-page [{:keys [editing] :as props}]
  (if editing
    [edit-content-item-page props]
    [new-content-item-page props]))

(def config
  {:page-id :single-content-item-page
   :component single-content-item-page})

(ns rpub.plugins.admin.new-user-page
  (:require [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.plugins.admin.impl :as admin-impl]))

(defn page [_]
  (let [[{:keys [::field-values ::submitting]}
         push] (use-dag [::field-values ::submitting])
        submit-form (fn [_] (prn field-values))
        fields-config [{:key :username, :label "Username"}
                       {:key :password, :label "Password"}
                       {:key :roles, :label "Roles"}]
        update-field (fn [field-key e]
                       (let [value (-> e .-target .-value)]
                         (push ::change-input [field-key value])))]
    [:div {:class "p-4"}
     [admin-impl/box
      {:title "New User"
       :content
       [:section {:class "bg-white"}
        [:div {:class "max-w-2xl"}
         [:form {:on-submit submit-form}
          [:div {:class "grid gap-4 sm:grid-cols-2 sm:gap-6"}
           (for [field fields-config
                 :let [value (get field-values (:key field) (:value field))]]
             [:div {:key (str (:key field)) :class "sm:col-span-2"}
              [:label {:class "block mb-2 text-sm font-semibold text-gray-900" :for "name"}
               (:label field)]
              [html/input2
               {:type :text
                :name (:key field)
                :value value
                :on-change #(update-field (:key field) %)}]])
           [html/submit-button {:ready-label "Create"
                                :submit-label "Creating..."
                                :submitting submitting}]]]]]}]]))

(defn change-input [db [k v]]
  (assoc-in db [::inputs k :value] v))

(defn submit-error [db]
  (assoc db ::submitting false))

(defn submit-start [db]
  (assoc db ::submitting true))

(defn field-values
  ([db] (field-values db (keys (::inputs db))))
  ([db ks]
   (-> (::inputs db)
       (select-keys ks)
       (update-vals :value))))

(def dag-config
  {:nodes
   {::change-input {:push change-input}
    ::field-values {:calc field-values}
    ::submit-error {:push submit-error}
    ::submit-start {:push submit-start}
    ::submitting {:calc ::submitting}}

   :edges
   [[::change-input ::field-values]
    [::submit-error ::submitting]
    [::submit-start ::submitting]]})

(def config
  {:page-id :new-user-page
   :component page
   :dag-config dag-config})

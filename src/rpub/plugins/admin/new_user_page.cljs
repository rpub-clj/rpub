(ns rpub.plugins.admin.new-user-page
  (:require [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.impl :as admin-impl]))

(defn page [_]
  (let [[{:keys [::field-values ::submitting]}
         push] (use-dag [::field-values ::submitting])
        http-opts {:format :transit}
        submit-form (fn [e]
                      (.preventDefault e)
                      (push ::submit-start)
                      (let [on-complete (fn [_ err]
                                          (if err
                                            (push ::submit-error)
                                            (.reload (.-location js/window))))
                            http-opts' (merge http-opts {:body field-values
                                                         :on-complete on-complete})]
                        (http/post "/admin/api/create-user" http-opts')))
        fields-config [{:key :username, :label "Username", :type :text}
                       {:key :password, :label "Password", :type :password}
                       {:key :roles, :label "Roles", :type :text}]
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
               {:type (:type field)
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

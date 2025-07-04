(ns rpub.plugins.admin.single-theme-page
  (:require [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.plugins.admin.impl :as admin-impl]))

(defn html-input [{:keys [value on-change]}]
  [:textarea {:class "w-full min-h-96 font-app-mono bg-gray-50 text-sm"
              :value value
              :on-change on-change}])

(defn field [{:keys [field-config input-component]}]
  (let [[{:keys [::field-values]} push] (use-dag [::field-values])
        value (get field-values (:key field-config) (:value field-config))
        update-field (fn [field-key e]
                       (let [value (-> e .-target .-value)]
                         (push ::change-input [field-key value])))]
    [:div {:key (str (:key field-config)) :class "sm:col-span-2"}
     [:label {:class "block mb-2 text-sm font-semibold text-gray-900" :for "name"}
      (:label field-config)]
     [input-component
      {:type (:type field-config)
       :name (:key field-config)
       :value value
       :on-change #(update-field (:key field-config) %)}]]))

(defn page [{:keys [theme]}]
  (let [[{:keys [::submitting]} #_push] (use-dag [::submitting])
        submit-form (fn [e] (.preventDefault e))
        html-template (get-in theme [:value :html-template])]
    [:div {:class "p-4"}
     [admin-impl/box
      {:class "mb-4"
       :title (if (:new theme)
                "New Theme"
                [:div
                 [:span.italic.text-blue-600 "Theme: "]
                 (:label theme)])}]
     [admin-impl/box
      {:content
       [:form {:on-submit submit-form}
        [:div {:class "grid gap-4 sm:grid-cols-2 sm:gap-6"}
         [:div {:class "max-w-2xl"}
          [field {:field-config {:key :name
                                 :label "Name"
                                 :type :text
                                 :value (:label theme)}
                  :input-component html/input2}]]
         [field {:field-config {:key :html
                                :label "HTML"
                                :value html-template}
                 :input-component html-input}]
         (if (:new theme)
           [html/submit-button {:ready-label "Create"
                                :submit-label "Creating..."
                                :submitting submitting}]
           [html/submit-button {:ready-label "Save"
                                :submit-label "Saving..."
                                :submitting submitting}])]]}]]))

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
  {:page-id :single-theme-page
   :component page
   :dag-config dag-config})

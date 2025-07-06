(ns rpub.plugins.admin.themes.single-theme-page
  (:require [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.helpers :as helpers]))

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
     [:label {:class "block mb-2 text-sm font-semibold text-gray-900" :for "label"}
      (:label field-config)]
     [input-component
      {:type (:type field-config)
       :name (:key field-config)
       :value value
       :on-change #(update-field (:key field-config) %)}]]))

(defn page [{:keys [theme]}]
  (let [[{:keys [::submitting ::field-values]}] (use-dag [::submitting ::field-values])
        submit-form (fn [e]
                      (.preventDefault e)
                      (let [body {:theme (-> theme
                                             (merge field-values)
                                             (assoc :value {:html-template (or (:html-template field-values)
                                                                               (get-in theme [:value :html-template]))})
                                             (dissoc :html-template))}]
                        (if (:new theme)
                          (-> (http/post "/admin/api/create-theme" {:body body})
                              (.then (fn [_]
                                       (set! js/window.location "/admin/themes"))))
                          (-> (http/post "/admin/api/update-theme" {:body body})
                              (.then (fn [_] (.reload js/window.location)))))))
        handle-delete (fn [_]
                        (let [msg (str "Are you sure want to delete the \""
                                       (:label theme)
                                       "\" theme?")]
                          (when (js/confirm msg)
                            (let [body (select-keys theme [:id])]
                              (-> (http/post "/admin/api/delete-theme" {:body body})
                                  (.then (fn [_]
                                           (set! js/window.location "/admin/themes"))))))))

        html-template (get-in theme [:value :html-template])]
    [:div {:class "p-4"}
     [helpers/box
      {:class "mb-4"
       :title [:div.flex
               [:div.grow
                (if (:new theme)
                  "New Theme"
                  [:div
                   [:span.italic.text-blue-600 "Theme: "]
                   (:label theme)])]
               [:div
                (when-not (:new theme)
                  [html/delete-button {:on-click handle-delete}
                   "Delete"])]]}]
     [helpers/box
      {:content
       [:form {:on-submit submit-form}
        [:div {:class "grid gap-4 sm:grid-cols-2 sm:gap-6"}
         [:div {:class "max-w-2xl"}
          [field {:field-config {:key :label
                                 :label "Name"
                                 :type :text
                                 :value (:label theme)}
                  :input-component html/input2}]]
         [field {:field-config {:key :html-template
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

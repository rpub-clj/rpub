(ns rpub.plugins.admin.themes.single-theme-page
  (:require ["react" :refer [useEffect]]
            [rpub.lib.dag.react :refer [use-dag use-dag-values]]
            [rpub.lib.forms :as forms]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.helpers :as helpers]))

(def form-schema
  {:label {:valid #(and (string? %) (seq %))
           :message "Required"}
   :html-template {:valid #(and (string? %) (seq %))
                   :message "Required"}})

(defn html-input [{:keys [value valid on-change]}]
  [:textarea {:class (str "w-full min-h-96 font-app-mono text-sm rounded-[6px] "
                          (if valid
                            "border-gray-300 focus:ring-primary-600 focus:border-blue-600"
                            "bg-red-50 border-red-500 focus:ring-red-500"))
              :value value
              :on-change on-change}])

(defn field [{:keys [field-config input-component]}]
  (let [[v push] (use-dag [[::field-values [(:key field-config)]]])
        field-values (get v [::field-values [(:key field-config)]])
        field-value (get field-values (:key field-config))
        {:keys [message valid]} field-value
        value (get field-value :value (:value field-config))
        update-field (fn [field-key e]
                       (let [v (-> e .-target .-value)]
                         (push ::change-input [field-key v])))]
    [:div {:key (str (:key field-config)) :class "sm:col-span-2"}
     [:label {:class "block mb-2 text-sm text-gray-900" :for "label"}
      [:span {:class "font-semibold"}
       (:label field-config)]
      (when message
        [:span {:class "text-red-500"} " - " message])]
     [input-component
      {:type (:type field-config)
       :valid (or valid (not (contains? field-value :value)))
       :name (:key field-config)
       :value value
       :on-change #(update-field (:key field-config) %)}]]))

(defn submit-form [e theme dag-values]
  (.preventDefault e)
  (let [{:keys [::field-values]} (dag-values)
        body {:theme (-> theme
                         (merge (update-vals :value field-values))
                         (assoc :value {:html-template (or (:html-template field-values)
                                                           (get-in theme [:value :html-template]))})
                         (dissoc :html-template))}]
    (if (:new theme)
      (-> (http/post "/admin/api/create-theme" {:body body})
          (.then (fn [_]
                   (set! js/window.location "/admin/themes"))))
      (-> (http/post "/admin/api/update-theme" {:body body})
          (.then (fn [_] (.reload js/window.location)))))))

(defn handle-delete [_ theme]
  (let [msg (str "Are you sure want to delete the \""
                 (:label theme)
                 "\" theme?")]
    (when (js/confirm msg)
      (let [body (select-keys theme [:id])]
        (-> (http/post "/admin/api/delete-theme" {:body body})
            (.then (fn [_]
                     (set! js/window.location "/admin/themes"))))))))

(defn page [{:keys [theme] :as props}]
  (let [[{:keys [::submitting ::ready-to-submit]}
         push] (use-dag [::submitting ::ready-to-submit])
        dag-values (use-dag-values [::field-values])
        _ (useEffect (fn [] (push ::init props)) #js[])
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
                  [html/delete-button {:on-click #(handle-delete % theme)}
                   "Delete"])]]}]
     [helpers/box
      {:content
       [:form {:on-submit #(submit-form % theme dag-values)}
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
                 :input-component html-input}]]
        (if (:new theme)
          [html/submit-button {:ready-label "Create"
                               :submit-label "Creating..."
                               :disabled (not ready-to-submit)
                               :submitting submitting}]
          [html/submit-button {:ready-label "Save"
                               :submit-label "Saving..."
                               :submitting submitting}])]}]]))

(def dag-config (forms/->dag ::form form-schema))

(def config
  {:page-id :single-theme-page
   :component page
   :dag-config dag-config})

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

(defn submit-form [e theme dag-values]
  (.preventDefault e)
  (let [{:keys [::forms/field-values]} (dag-values)
        body {:theme (-> theme
                         (merge (update-vals field-values :value))
                         (assoc :value {:html-template (or (:value (:html-template field-values))
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

(defn page [{:keys [theme] :as _props}]
  (let [form {:id ::form, :schema form-schema}
        [v push] (use-dag [[::forms/submitting form]
                           [::forms/ready-to-submit form]])
        submitting (get v [::forms/submitting form])
        ready-to-submit (get v [::forms/ready-to-submit form])
        dag-values (use-dag-values [[::forms/field-values form]])
        _ (useEffect (fn [] (push ::forms/init form)) #js[])
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
          [forms/field {:form form
                        :field-config {:key :label
                                       :label "Name"
                                       :type :text
                                       :value (:label theme)}
                        :input-component html/input2}]]
         [forms/field {:form form
                       :field-config {:key :html-template
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

(def dag-config forms/dag-config)

(def config
  {:page-id :single-theme-page
   :component page
   :dag-config dag-config})

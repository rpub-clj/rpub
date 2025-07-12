(ns rpub.plugins.admin.users.single-user-page
  (:require ["react" :refer [useEffect]]
            [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.forms :as forms]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.helpers :as helpers]))

(def form-schema
  {:username {:valid #(and (string? %) (seq %) (>= (count %) 3))
              :message "Must be at least 3 characters"}
   :password {:valid #(and (string? %) (seq %) (>= (count %) 6))
              :message "Must be at least 6 characters"}
   :roles {:valid #(and (string? %) (seq %))
           :message "Required"}})

(defn html-input [{:keys [value valid on-change type name]}]
  [html/input2
   {:type type
    :name name
    :value value
    :class (if (false? valid) "border-red-500" "")
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

(defn new-user-page [_]
  (let [[{:keys [::submitting ::ready-to-submit]}
         push] (use-dag [::submitting ::ready-to-submit])
        _ (useEffect (fn [] (push ::init)) #js[])
        submit-form (fn [e]
                      (.preventDefault e)
                      (when ready-to-submit
                        (push ::submit-start)
                        (-> (http/post "/admin/api/create-user" {:body {} #_field-values})
                            (.then (fn [_] (.reload (.-location js/window))))
                            (.catch (fn [_] (push ::submit-error))))))
        fields-config [{:key :username, :label "Username", :type :text}
                       {:key :password, :label "Password", :type :password}
                       #_{:key :roles, :label "Roles", :type :text}]]
    [:div {:class "p-4"}
     [helpers/box
      {:title "New User"
       :content
       [:section {:class "bg-white"}
        [:div {:class "max-w-2xl"}
         [:form {:on-submit submit-form}
          [:div {:class "grid gap-4 sm:grid-cols-2 sm:gap-6"}
           (for [field-config fields-config]
             [field {:key (:key field-config)
                     :field-config field-config
                     :input-component html-input}])
           [html/submit-button {:ready-label "Create"
                                :submit-label "Creating..."
                                :submitting submitting
                                :disabled (not ready-to-submit)}]]]]]}]]))

(defn edit-user-page [_])

(defn- single-user-page [{:keys [editing] :as props}]
  (if editing
    [edit-user-page props]
    [new-user-page props]))

(def dag-config (forms/->dag ::form form-schema))

(def config
  {:page-id :single-user-page
   :component single-user-page
   :dag-config dag-config})

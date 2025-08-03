(ns rpub.plugins.admin.users.single-user-page
  (:require ["react" :refer [useEffect]]
            [rpub.lib.forms :as forms]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.lib.substrate :refer [subscribe dispatch]]
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

(defn new-user-page [_]
  (let [form {:id ::form, :schema form-schema}
        submitting (subscribe [::forms/submitting form])
        ready-to-submit (subscribe [::forms/ready-to-submit form])
        _ (useEffect (fn [] (dispatch [::forms/init form])) #js[])
        submit-form (fn [e]
                      (.preventDefault e)
                      (when ready-to-submit
                        (dispatch ::submit-start)
                        (-> (http/post "/admin/api/create-user" {:body {} #_field-values})
                            (.then (fn [_] (.reload (.-location js/window))))
                            (.catch (fn [_] (dispatch ::submit-error))))))
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
             [forms/field {:form form
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

(def config
  {:page-id :single-user-page
   :component single-user-page})

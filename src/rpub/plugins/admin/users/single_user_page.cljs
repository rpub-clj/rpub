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

(defn html-input [{:keys [value touched valid on-change on-blur type name]}]
  [html/input2
   {:type type
    :name name
    :value value
    :class (if (or valid (not touched)) "" "border-red-500")
    :on-blur on-blur
    :on-change on-change}])

(defn page [_]
  (let [form {:id ::form
              :schema form-schema
              :initial-values {:username "", :password "", :roles ""}}
        submitting (subscribe [::forms/submitting form])
        field-values (subscribe [::forms/field-values form])
        _ (useEffect (fn [] (dispatch [::forms/init form])) #js[])
        submit-form (fn [_]
                      (let [body (update-vals field-values :value)]
                        (dispatch [::forms/submit-start])
                        (prn 'started)
                        (-> (http/post "/admin/api/create-user" {:body body})
                            (.then (fn [_] (set! js/window.location "/admin/users")))
                            (.catch (fn [_] (dispatch [::forms/submit-error]))))))
        fields-config [{:key :username, :label "Username", :type :text}
                       {:key :password, :label "Password", :type :password}
                       {:key :roles, :label "Roles", :type :text}]]
    [:div {:class "p-4"}
     [helpers/box
      {:title "New User"
       :content
       [:section {:class "bg-white"}
        [:div {:class "max-w-2xl"}
         [:form {:on-submit (fn [e]
                              (.preventDefault e)
                              (dispatch [::forms/submit-form form submit-form]))}
          [:div {:class "grid gap-4 sm:grid-cols-2 sm:gap-6"}
           (for [field-config fields-config]
             [forms/field {:form form
                           :field-config field-config
                           :input-component html-input}])
           [html/submit-button {:ready-label "Create"
                                :submit-label "Creating..."
                                :submitting submitting}]]]]]}]]))

(def config
  {:page-id :single-user-page
   :component page})

(ns rpub.plugins.admin.settings.page
  (:require [rpub.lib.forms :as forms]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.lib.substrate :refer [subscribe dispatch]]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- page [{:keys [settings] :as _props}]
  (let [form {:id ::form}
        field-values (subscribe [::forms/field-values form])
        submitting (subscribe [::forms/submitting form])
        settings-index (helpers/index-by :key settings)
        update-setting (fn [setting-key e]
                         (let [value (-> e .-target .-value)]
                           (dispatch [::forms/change-input form setting-key value])))
        submit-form (fn [e]
                      (.preventDefault e)
                      (let [settings (-> (merge-with #(assoc %1 :value (:value %2))
                                                     settings-index
                                                     field-values)
                                         (update-vals #(select-keys % [:key :value]))
                                         vals)]
                        (-> (http/post "/admin/api/update-settings" {:body {:settings settings}})
                            (.then (fn [] (.reload (.-location js/window))))
                            (.catch (fn [_] (dispatch [::forms/submit-error form]))))))]
    [:div {:class "p-4"}
     [helpers/box
      {:title "Settings"
       :content
       [:section {:class "bg-white"}
        [:div {:class "max-w-2xl"}
         [:form {:on-submit submit-form}
          [:div {:class "grid gap-4 sm:grid-cols-2 sm:gap-6"}
           (for [setting (sort-by :label (vals settings-index))]
             [:div {:key (:key setting) :class "sm:col-span-2"}
              [:label {:class "block mb-2 text-sm font-semibold text-gray-900" :for "name"}
               (:label setting)]
              (prn setting)
              [html/input2
               {:type :text
                :name (:key setting)
                :value (or (get-in field-values [(:key setting) :value]) (:value setting))
                :on-change #(update-setting (:key setting) %)}]])
           [html/submit-button {:ready-label "Save"
                                :submit-label "Saving..."
                                :submitting submitting}]]]]]}]]))

(def config
  {:page-id :settings-page
   :component page})

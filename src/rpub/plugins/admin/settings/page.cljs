(ns rpub.plugins.admin.settings.page
  (:require [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- page [{:keys [settings] :as _props}]
  (let [[{:keys [::field-values ::submitting]}
         push] (use-dag [::field-values ::submitting])
        settings-index (helpers/index-by :key settings)
        update-setting (fn [setting-key e]
                         (let [value (-> e .-target .-value)]
                           (push ::change-input [setting-key value])))
        submit-form (fn [e]
                      (.preventDefault e)
                      (push ::submit-start)
                      (let [settings (-> (merge-with #(assoc %1 :value %2)
                                                     settings-index
                                                     field-values)
                                         (update-vals #(select-keys % [:key :value]))
                                         vals)]
                        (-> (http/post "/admin/api/update-settings" {:body {:settings settings}})
                            (.then (fn [] (.reload (.-location js/window))))
                            (.catch (fn [_] (push ::submit-error))))))]
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
                :value (or (get field-values (:key setting)) (:value setting))
                :on-change #(update-setting (:key setting) %)}]])
           [html/submit-button {:ready-label "Save"
                                :submit-label "Saving..."
                                :submitting submitting}]]]]]}]]))

(defn change-input [db [k v]]
  (assoc-in db [:inputs k :value] v))

(defn submit-start [db]
  (assoc db ::submitting true))

(defn submit-error [db]
  (assoc db ::submitting false))

(def dag-config
  {:nodes
   {::change-input {:push change-input}
    ::field-values {:calc helpers/field-values}
    ::submit-error {:push submit-error}
    ::submit-start {:push submit-start}
    ::submitting {:calc ::submitting}}

   :edges
   [[::change-input ::field-values]
    [::submit-error ::submitting]
    [::submit-start ::submitting]]})

(def config
  {:page-id :settings-page
   :component page
   :dag-config dag-config})

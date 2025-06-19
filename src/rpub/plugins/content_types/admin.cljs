(ns rpub.plugins.content-types.admin
  (:require [rpub.lib.html :as html]
            [rpub.lib.reagent :as r]
            [rpub.plugins.admin.impl :as admin-impl]
            [rpub.plugins.content-types.admin.all-content-types-page
             :as all-content-types-page]
            [rpub.plugins.content-types.admin.single-content-item-page
             :as single-content-item-page]
            [rpub.plugins.content-types.admin.single-content-type-page
             :as single-content-type-page]))

(defn field-type-label [field]
  (:name field))

(defn rpub-field-types-text
  [{:keys [field editing creating value on-change] :as _props}]
  [html/input2 (cond-> {:type :text
                        :name (:name field)
                        :on-change on-change}
                 editing (assoc :value value)
                 creating (assoc :placeholder (field-type-label field)))])

(defn rpub-field-types-text-lg [{:keys [field editing creating value]}]
  [html/textarea (cond-> {:name (:name field)
                          :on-change prn}
                   editing (assoc :value value)
                   creating (assoc :placeholder (field-type-label field)))])

(defn rpub-field-types-datetime [_]
  [:div.relative.max-w-sm
   [:div.absolute.inset-y-0.start-0.flex.items-center.ps-3.5.pointer-events-none
    [:svg.w-4.h-4.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :fill "currentColor" :viewBox "0 0 20 20"}
     [:path {:d "M20 4a2 2 0 0 0-2-2h-2V1a1 1 0 0 0-2 0v1h-3V1a1 1 0 0 0-2 0v1H6V1a1 1 0 0 0-2 0v1H2a2 2 0 0 0-2 2v2h20V4ZM0 18a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8H0v10Zm5-8h10a1 1 0 0 1 0 2H5a1 1 0 0 1 0-2Z"}]]]
   [:input#default-datepicker
    {:class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full ps-10 p-2 5"
     :datepicker "datepicker"
     :datepicker-buttons "datepicker-buttons"
     :datepicker-autoselect-today "datepicker-autoselect-today"
     :type "text"
     :placeholder "Select date"}]])

(defn add-elements [& {:as _opts}]
  (html/add-element :rpub-field-types-text
                    (r/reactify-component rpub-field-types-text))
  (html/add-element :rpub-field-types-text-lg
                    (r/reactify-component rpub-field-types-text-lg))
  (html/add-element :rpub-field-types-datetime
                    (r/reactify-component rpub-field-types-datetime)))

(defn add-pages [& {:as opts}]
  (admin-impl/add-page (merge opts all-content-types-page/config))
  (admin-impl/add-page (merge opts single-content-item-page/config))
  (admin-impl/add-page (merge opts single-content-type-page/config)))

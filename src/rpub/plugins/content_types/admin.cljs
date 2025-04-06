(ns rpub.plugins.content-types.admin
  (:require [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]
            [rpub.lib.reagent :as r]
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
  [:input {:type "datetime-local"}])

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

(ns rpub.lib.forms
  (:require [rpub.lib.dag :as dag]
            [rpub.lib.dag.react :refer [use-dag]]))

(defn validate [form k v]
  (when-let [{:keys [valid message]} (get (:schema form) k)]
    (if (valid v)
      {:valid true}
      {:valid false, :message message})))

(defn change-input [db [form k v]]
  (-> db
      (assoc-in [::inputs (:id form) k :value] v)
      (update-in [::inputs (:id form) k] dissoc :valid :message)
      (update-in [::inputs (:id form) k] merge (validate form k v))))

(defn submit-error [db form]
  (assoc-in db [::submitting (:id form)] false))

(defn submit-start [db form]
  (assoc-in db [::submitting (:id form)] true))

(defn init [db form]
  (assoc-in db
            [::inputs (:id form)]
            (update-vals (:schema form) (constantly {}))))

(defn field-values
  ([db] (::inputs db))
  ([db [form ks]] (select-keys (get (field-values db) (:id form)) ks)))

(defn ready-to-submit
  ([db] (::field-values (::dag/values db)))
  ([db form] (every? :valid (vals (get (ready-to-submit db) (:id form))))))

(defn submitting
  ([db] (::submitting db))
  ([db form] (get (submitting db) (:id form))))

(def dag-config
  {:nodes
   {::init {:push init}
    ::change-input {:push change-input}
    ::field-values {:calc field-values}
    ::submit-error {:push submit-error}
    ::submit-start {:push submit-start}
    ::submitting {:calc submitting}
    ::ready-to-submit {:calc ready-to-submit}}

   :edges
   [[::init ::field-values]
    [::change-input ::field-values]
    [::field-values ::ready-to-submit]
    [::submit-error ::submitting]
    [::submit-start ::submitting]]})

(defn field [{:keys [form field-config input-component]}]
  (let [[v push] (use-dag [[::field-values [form [(:key field-config)]]]])
        field-value (get-in v [[::field-values [form [(:key field-config)]]]
                               (:key field-config)])
        {:keys [message valid]} field-value
        value (get field-value :value (:value field-config))
        update-field (fn [field-key e]
                       (let [v (-> e .-target .-value)]
                         (push ::change-input [form field-key v])))]
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

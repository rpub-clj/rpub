(ns rpub.lib.forms
  (:require [rpub.lib.substrate :refer [subscribe dispatch]]))

(defn validate [form k v]
  (when-let [{:keys [valid message]} (get (:schema form) k)]
    (if (valid v)
      {:valid true}
      {:valid false, :message message})))

(defn change-input [db [_ form k v]]
  (-> db
      (assoc-in [::inputs (:id form) k :value] v)
      (update-in [::inputs (:id form) k] dissoc :valid :message)
      (update-in [::inputs (:id form) k] merge (validate form k v))))

(defn submit-error [db [_ form]]
  (assoc-in db [::submitting (:id form)] false))

(defn submit-start [db [_ form]]
  (assoc-in db [::submitting (:id form)] true))

(defn init [db [_ form]]
  (let [inputs (reduce-kv (fn [acc k v]
                            (assoc acc k (merge {:value v} (validate form k v))))
                          {}
                          (:initial-values form))]
    (assoc-in db [::inputs (:id form)] inputs)))

(defn field-values [db [_ form & ks]]
  (cond-> (get (::inputs db) (:id form))
    (seq ks) (get-in ks)))

(defn ready-to-submit [db [_ form]]
  (let [v (vals (get-in db [::inputs (:id form)]))]
    (and (seq v) (every? :valid v))))

(defn submitting [db [_ form]]
  (get (::submitting db) (:id form)))

(def model
  {:queries
   {::field-values field-values
    ::ready-to-submit ready-to-submit
    ::submitting submitting}

   :transactions
   {::init init
    ::change-input change-input
    ::submit-start submit-start
    ::submit-error submit-error}})

(defn- input [{:keys [form field-config input-component]}]
  (let [field-value (subscribe [::field-values form (:key field-config)])
        {:keys [valid value]} field-value
        update-field (fn [field-key e]
                       (let [v (-> e .-target .-value)]
                         (dispatch [::change-input form field-key v])))]
    [input-component
     {:type (:type field-config)
      :valid (or valid (not (contains? field-value :value)))
      :name (:key field-config)
      :value value
      :on-change #(update-field (:key field-config) %)}]))

(defn field [{:keys [form field-config] :as props}]
  (let [message (subscribe [::field-values form (:key field-config) :message])]
    [:div {:key (str (:key field-config)) :class "sm:col-span-2"}
     [:label {:class "block mb-2 text-sm text-gray-900" :for "label"}
      [:span {:class "font-semibold"}
       (:label field-config)]
      (when message
        [:span {:class "text-red-500"} " - " message])]
     [input props]]))

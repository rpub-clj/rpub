(ns rpub.lib.forms
  (:require [rpub.lib.substrate :refer [subscribe dispatch]]))

(defn validate-single-input [form k v]
  (when-let [{:keys [valid message]} (get (:schema form) k)]
    (if (valid v)
      {:valid true}
      {:valid false, :message message})))

(defn validate-all-inputs [form inputs]
  (reduce-kv (fn [acc k v]
               (assoc acc k (merge {:value v} (validate-single-input form k v))))
             {}
             inputs))

(defn change-input [db [_ form k v]]
  (-> db
      (assoc-in [::inputs (:id form) k :value] v)
      (update-in [::inputs (:id form) k] dissoc :valid :message)
      (update-in [::inputs (:id form) k] merge (validate-single-input form k v))))

(defn blur-input [db [_ form k]]
  (assoc-in db [::inputs (:id form) k :touched] true))

(defn submit-error [db [_ form]]
  (assoc-in db [::submitting (:id form)] false))

(defn submit-start [db [_ form]]
  (-> db
      (assoc-in [::submitting (:id form)] true)
      (update-in [::inputs (:id form)] update-vals #(assoc % :touched true))))

(defn init [db [_ form]]
  (let [inputs (validate-all-inputs form (:initial-values form))]
    (assoc-in db [::inputs (:id form)] inputs)))

(defn field-values [db [_ form & ks]]
  (cond-> (get (::inputs db) (:id form))
    (seq ks) (get-in ks)))

(defn submitting [db [_ form]]
  (get (::submitting db) (:id form)))

(defn submit-form [db [_ form callback]]
  (let [inputs (-> (get-in db [::inputs (:id form)])
                   (update-vals #(assoc % :touched true)))]
    (when (and (seq inputs) (every? :valid (vals inputs)))
      (callback))
    (assoc-in db [::inputs (:id form)] inputs)))

(def model
  {:queries
   {::field-values field-values
    ::submitting submitting}

   :transactions
   {::init init
    ::change-input change-input
    ::blur-input blur-input
    ::submit-form submit-form
    ::submit-start submit-start
    ::submit-error submit-error}})

(defn- input [{:keys [form field-config input-component]}]
  (let [field-value (subscribe [::field-values form (:key field-config)])
        {:keys [valid touched value]} field-value
        update-field (fn [field-key e]
                       (let [v (-> e .-target .-value)]
                         (dispatch [::change-input form field-key v])))]
    [input-component
     {:type (:type field-config)
      :valid (or valid (not (contains? field-value :value)))
      :touched touched
      :name (:key field-config)
      :value value
      :on-blur (fn [_] (dispatch [::blur-input form (:key field-config)]))
      :on-change #(update-field (:key field-config) %)}]))

(defn field [{:keys [form field-config] :as props}]
  (let [field-value (subscribe [::field-values form (:key field-config)])
        {:keys [touched message]} field-value]
    [:div {:key (str (:key field-config)) :class "sm:col-span-2"}
     [:label {:class "block mb-2 text-sm text-gray-900" :for "label"}
      [:span {:class "font-semibold"}
       (:label field-config)]
      (when (and touched message)
        [:span {:class "text-red-500"} " - " message])]
     [input props]]))

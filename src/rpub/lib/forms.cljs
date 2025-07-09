(ns rpub.lib.forms
  (:require [rpub.lib.dag :as dag]))

(defn validate [form-schema k v]
  (when-let [{:keys [valid message]} (get form-schema k)]
    (if (valid v)
      {:valid true}
      {:valid false, :message message})))

(defn change-input* [{:keys [inputs form-schema]} db [k v]]
  (-> db
      (assoc-in [inputs k :value] v)
      (update-in [inputs k] dissoc :valid :message)
      (update-in [inputs k] merge (validate form-schema k v))))

(defn submit-error* [{:keys [submitting]} db]
  (assoc db submitting false))

(defn submit-start* [{:keys [submitting]} db]
  (assoc db submitting true))

(defn init* [{:keys [inputs form-schema]} db _]
  (assoc db inputs (update-vals form-schema (constantly {}))))

(defn field-values*
  ([{:keys [inputs] :as config} db]
   (field-values* config db (keys (get db inputs))))
  ([{:keys [inputs]} db ks]
   (select-keys (get db inputs) ks)))

(defn ready-to-submit* [{:keys [field-values]} {:keys [::dag/values]}]
  (every? :valid (vals (get values field-values))))

(defn ->dag [ns-key form-schema]
  (let [config (merge (->> [:init :change-input :field-values :submit-error
                            :submit-start :submitting :ready-to-submit :inputs]
                           (map (fn [k] [k (keyword (namespace ns-key) (name k))]))
                           (into {}))
                      {:form-schema form-schema})
        {:keys [init change-input field-values submit-error submit-start
                submitting ready-to-submit]} config]
    {:nodes
     {init {:push (partial init* config)}
      change-input {:push (partial change-input* config)}
      field-values {:calc (partial field-values* config)}
      submit-error {:push (partial submit-error* config)}
      submit-start {:push (partial submit-start* config)}
      submitting {:calc submitting}
      ready-to-submit {:calc (partial ready-to-submit* config)}}

     :edges
     [[init field-values]
      [change-input field-values]
      [field-values ready-to-submit]
      [submit-error submitting]
      [submit-start submitting]]}))

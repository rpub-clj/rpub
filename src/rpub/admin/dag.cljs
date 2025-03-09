(ns rpub.admin.dag
  (:require [rpub.lib.dag :as dag]))

(defn init [_ v] v)

(defn field-values
  ([db] (field-values db (keys (:inputs db))))
  ([db ks]
   (-> (:inputs db)
       (select-keys ks)
       (update-vals :value))))

(defn model-settings [db]
  (merge (:settings db) (field-values db)))

(defn change-input [db [k v]]
  (assoc-in db [:inputs k :value] v))

(defn restart-server [db _]
  (assoc db :restarted true))

(defn submit-start [db _]
  (assoc db :settings-page/submitting true))

(defn submit-error [db _]
  (assoc db :settings-page/submitting false))

(def dag-config
  {:nodes
   {:settings-page/field-values {:calc field-values}
    :init {:push init}
    :model/settings {:calc model-settings}
    :settings-page/change-input {:push change-input}
    :settings-page/submit-start {:push submit-start}
    :settings-page/submit-error {:push submit-error}
    :settings-page/submitting {:calc :settings-page/submitting}
    :settings-page/restart-server {:push restart-server}
    :settings-page/update-settings {:push change-input}
    :model/site-url {:calc (comp :site-url :settings)}}

   :edges
   [[:init :model/settings]
    [:settings-page/update-settings :model/settings]
    [:settings-page/submit-start :settings-page/submitting]
    [:settings-page/submit-error :settings-page/submitting]
    [:model/settings :settings-page/field-values]
    [:model/settings :model/site-url]
    [:settings-page/change-input :settings-page/field-values]]})

(defonce dag-atom (atom (dag/->dag dag-config)))

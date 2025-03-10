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

(defn activate-plugin [db k]
  (-> db
      (assoc :plugins-page/needs-restart true)
      (update :plugins-page/activated-plugins (fnil conj #{}) k)))

(defn deactivate-plugin [db k]
  (-> db
      (assoc :plugins-page/needs-restart true)
      (update :plugins-page/activated-plugins disj k)))

(defn restart-server [db]
  (assoc db :plugins-page/restarted true))

(defn submit-start [db]
  (assoc db :settings-page/submitting true))

(defn submit-error [db]
  (assoc db :settings-page/submitting false))

(defn activate-theme [db theme-label]
  (assoc db :themes-page/current-theme-name-setting {:value theme-label}))

(def dag-config
  {:nodes
   {:init {:push init}
    :model/settings {:calc model-settings}
    :model/site-url {:calc (comp :site-url :settings)}
    :plugins-page/needs-restart {:calc :plugins-page/needs-restart}
    :plugins-page/restart-server {:push restart-server}
    :plugins-page/activated-plugins {:calc :plugins-page/activated-plugins}
    :plugins-page/activate-plugin {:push activate-plugin}
    :plugins-page/deactivate-plugin {:push deactivate-plugin}
    :settings-page/change-input {:push change-input}
    :settings-page/field-values {:calc field-values}
    :settings-page/submit-error {:push submit-error}
    :settings-page/submit-start {:push submit-start}
    :settings-page/submitting {:calc :settings-page/submitting}
    :settings-page/update-settings {:push change-input}
    :themes-page/activate-theme {:push activate-theme}
    :themes-page/current-theme-name-setting {:calc :themes-page/current-theme-name-setting}}

   :edges
   [[:init :model/settings]
    [:model/settings :model/site-url]
    [:model/settings :settings-page/field-values]
    [:plugins-page/activate-plugin :plugins-page/needs-restart]
    [:plugins-page/activate-plugin :plugins-page/activated-plugins]
    [:plugins-page/deactivate-plugin :plugins-page/needs-restart]
    [:plugins-page/deactivate-plugin :plugins-page/activated-plugins]
    [:settings-page/change-input :settings-page/field-values]
    [:settings-page/submit-error :settings-page/submitting]
    [:settings-page/submit-start :settings-page/submitting]
    [:settings-page/update-settings :model/settings]
    [:themes-page/activate-theme :themes-page/current-theme-name-setting]]})

(defonce dag-atom
  (atom (-> (dag/->dag dag-config)
            #_dag/add-tracing)))

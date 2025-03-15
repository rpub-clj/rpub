(ns rpub.admin.dag
  (:require [rpub.admin.impl :as admin-impl]
            [rpub.lib.dag :as dag]))

(defn init [_ {:keys [content-types]}]
  (let [content-types-index (admin-impl/index-by :id content-types)]
    {:model/content-types-index content-types-index}))

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

(defn select-content-type [db {:keys [content-type]}]
  (let [selection {:content-type (select-keys content-type [:id])}]
    (assoc db :all-content-types-page/selection selection)))

(defn select-content-type-field [db {:keys [content-type content-type-field]}]
  (let [selection {:content-type (select-keys content-type [:id])
                   :content-type-field (select-keys content-type-field [:id])}]
    (assoc db :all-content-types-page/selection selection)))

(defn clear-selection [db]
  (dissoc db :all-content-types-page/selection))

(defn selection [db]
  (let [sel (get db :all-content-types-page/selection)
        content-type-id (get-in sel [:content-type :id])
        content-type-field-id (get-in sel [:content-type-field :id])
        content-type (get-in db [:model/content-types-index content-type-id])]
    (cond-> nil
      (:content-type sel)
      (assoc :content-type content-type)

      (:content-type-field sel)
      (assoc :content-type-field
             (->> (:fields content-type)
                  (filter #(= (:id %) content-type-field-id))
                  first)))))

(defn drag-start [db drag-source]
  (assoc db :all-content-types-page/drag-source drag-source))

(defn drag-drop [db {:keys [content-type]}]
  (let [{:keys [:all-content-types-page/drag-source]} db]
    (-> db
        (dissoc :all-content-types-page/drag-source)
        (update-in [:model/content-types-index (:id content-type) :fields]
                   conj
                   {:id (random-uuid)
                    :name (str "New " (:label drag-source))
                    :type (:type drag-source)
                    :created-at (js/Date.)
                    :created-by (uuid "00000000-0000-0000-0000-000000000000")
                    :rank (inc (apply max (map :rank (:fields content-type))))}))))

(def dag-config
  {:nodes
   {:all-content-types-page/clear-selection {:push clear-selection}
    :all-content-types-page/select-content-type {:push select-content-type}
    :all-content-types-page/select-content-type-field {:push select-content-type-field}
    :all-content-types-page/selection {:calc selection}
    :all-content-types-page/drag-start {:push drag-start}
    :all-content-types-page/drag-drop {:push drag-drop}
    :init {:push init}
    :model/content-types-index {:calc :model/content-types-index}
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
   [[:all-content-types-page/clear-selection :all-content-types-page/selection]
    [:all-content-types-page/select-content-type :all-content-types-page/selection]
    [:all-content-types-page/select-content-type-field :all-content-types-page/selection]
    [:all-content-types-page/drag-drop :model/content-types-index]
    [:init :model/settings]
    [:init :model/content-types-index]
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
            dag/add-tracing)))

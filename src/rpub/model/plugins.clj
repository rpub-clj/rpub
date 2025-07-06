(ns rpub.model.plugins
  (:require [medley.core :as medley]
            [rpub.model.common :as common]))

(defmulti internal-plugin (fn [k] k))

^:clj-reload/keep
(defprotocol Model
  (-get-plugins [model opts])
  (-update-plugin! [model plugin]))

(defn- coerce [model]
  (get-in model [:models :plugins] model))

(defn get-plugins [model opts]
  (-get-plugins (coerce model) opts))

(defn update-plugin! [model opts]
  (-update-plugin! (coerce model) opts))

(defn- ->installed-plugins [registered-plugins]
  (->> registered-plugins
       (map (fn [[k plugin]]
              (merge plugin {:key k, :installed true})))))

(defn ->plugins [registered-plugins {:keys [model] :as _opts}]
  (let [internal-plugins (->> (methods internal-plugin)
                              (map (fn [[k f]]
                                     [k (-> (f k) (assoc :rpub/internal true))]))
                              (into {}))
        all-plugins (concat internal-plugins registered-plugins)
        activated-plugins (get-plugins model {})
        activated-index (medley/index-by :key activated-plugins)
        installed-plugins (->installed-plugins all-plugins)
        installed-index (medley/index-by :key installed-plugins)]
    (vals (merge-with merge installed-index activated-index))))

(defn ->plugin [{:keys [id key current-user] :as opts}]
  (-> {:id (or id (random-uuid))
       :key key}
      (merge (select-keys opts [:activated :label :sha]))
      (common/add-metadata current-user)))

(defn plugin-visible? [{:keys [rpub/internal installed]}]
  (and (not internal) installed))

(defn initial-plugins [{:keys [current-user] :as _opts}]
  (->> [{:key :rpub.plugins.starter-theme/plugin, :activated true}
        {:key :rpub.plugins.external-editing/plugin, :activated true}
        {:key :rpub.plugins.content-types/plugin, :activated true}
        {:key :rpub.plugins.admin/plugin, :activated true}
        {:key :rpub.plugins.app/plugin, :activated true}]
       (map #(->plugin (assoc % :current-user current-user)))))

(defmulti ->model :db-type)

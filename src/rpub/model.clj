(ns rpub.model
  {:no-doc true}
  (:require [buddy.hashers :as hashers]
            [medley.core :as medley]
            [rads.inflections :as inflections])
  (:import (java.time Instant)))

(defmulti internal-plugin (fn [k] k))

^:clj-reload/keep
(defprotocol Model
  (db-info [model])
  (migrate! [model opts])
  (get-users [model opts])
  (create-user! [model user])
  (get-settings [model opts])
  (create-setting! [model setting])
  (update-setting! [model setting])
  (get-plugins [model opts])
  (update-plugin! [model plugin]))

(defn ->slug [title]
  (inflections/parameterize title))

(defn add-metadata [{:keys [created-at created-by] :as entity} current-user]
  (cond-> entity
    created-at (assoc :updated-at (Instant/now))
    created-by (assoc :updated-by (:id current-user))
    (not created-at) (assoc :created-at (Instant/now))
    (not created-by) (assoc :created-by (:id current-user))))

(defn ->user [& {:keys [id username password current-user] :as _opts}]
  (-> {:id (or id (random-uuid))
       :username username
       :password-hash (hashers/derive password)}
      (add-metadata current-user)))

(defn verify-password [user attempt]
  (:valid (hashers/verify attempt (:password-hash user))))

(defn get-current-user [{:keys [model current-user] :as _req}]
  (first (get-users model {:ids [(:id current-user)]})))

(defn ->setting [& {:keys [id key label value current-user] :as _opts}]
  (-> {:id (or id (random-uuid))
       :key key
       :value value
       :label label}
      (add-metadata current-user)))

(defn- ->installed-plugins [registered-plugins]
  (->> registered-plugins
       (map (fn [[k plugin]] (assoc plugin :key k)))))

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
      (add-metadata current-user)))

(defn active-theme [{:keys [settings themes] :as _req}]
  (let [theme-name-value (get-in settings [:theme-name :value])
        theme (medley/find-first #(= (:label %) theme-name-value) themes)]
    (or theme {:label theme-name-value})))

(defmulti ->model :db-type)

(comment
  (do
    (def taps (atom []))
    (defonce debug #(swap! taps conj %))
    (add-tap debug))

  (let [req (->> @taps (keep :req) last)
        {:keys [conn model]} req]
    (migrate! (assoc model :ds conn) req)))

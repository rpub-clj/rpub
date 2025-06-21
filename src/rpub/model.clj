(ns rpub.model
  {:no-doc true}
  (:require [buddy.hashers :as hashers]
            [medley.core :as medley]
            [rads.inflections :as inflections]
            [rpub.model.app :as-alias model-app])
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
  (update-plugin! [model plugin])
  (get-unsaved-changes [model opts])
  (update-unsaved-changes! [model unsaved-changes])
  (delete-unsaved-changes! [model opts])
  (get-apps [model opts])
  (create-app! [model app])
  (get-roles [model opts])
  (create-role! [model role])
  (get-user-roles [model opts])
  (create-user-role! [model user-role]))

(defn ->slug [title]
  (inflections/parameterize title))

(defn add-metadata [{:keys [created-at created-by app-id] :as entity} current-user]
  (cond-> entity
    (not app-id) (assoc :app-id (:app-id current-user))
    created-at (assoc :updated-at (Instant/now))
    created-by (assoc :updated-by (:id current-user))
    (not created-at) (assoc :created-at (Instant/now))
    (not created-by) (assoc :created-by (:id current-user))))

(defn ->user
  [& {:keys [id username password password-hash current-user]
      :as _opts}]
  (cond-> (-> {:id (or id (random-uuid))
               :username username}
              (add-metadata current-user))
    password-hash (assoc :password-hash password-hash)
    (not password-hash) (assoc :password-hash (hashers/derive password))))

(defn verify-password [user attempt]
  (:valid (hashers/verify attempt (:password-hash user))))

(defn get-current-user [{:keys [model] :as req}]
  (let [user-id (get-in req [:identity :id])]
    (first (get-users model {:ids [user-id], :roles true}))))

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

(defn ->unsaved-changes
  [{:keys [id key client-id value created-at created-by current-user]
    :as _opts}]
  (-> {:id (or id (random-uuid))
       :user-id (:id current-user)
       :client-id client-id
       :key key
       :value value
       :created-at created-at
       :created-by created-by}
      (add-metadata current-user)))

(def system-user
  {:id #uuid"00000000-0000-0000-0000-000000000000"})

(defn ->role [& {:keys [id label permissions current-user] :as _opts}]
  (-> {:id (or id (random-uuid))
       :label label
       :permissions permissions}
      (add-metadata current-user)))

(defn ->app [& {:keys [id domains new-user current-user] :as _opts}]
  (-> {:id (or id (random-uuid))
       :domains domains
       :new-user new-user}
      (add-metadata current-user)))

(defmulti ->model :db-type)

(defn initial-settings
  [{:keys [config
           site-title
           site-base-url
           current-user]
    :as _opts}]
  (->> [{:key :encrypted-session-store-key
         :label "Encrypted Session Store Key"
         :value (pr-str (:encrypted-session-store-key config))}

        {:key :site-title
         :label "Site Title"
         :value site-title}

        {:key :site-base-url
         :label "Site URL"
         :value site-base-url}

        {:key :permalink-single
         :label "Permalinks (Single)"
         :value "/:content-type-slug/:content-item-slug"}

        {:key :theme-name
         :label "Theme Name"
         :value "Starter Theme"}

        {:key :site-description
         :label "Site Description"
         :value "Write an awesome description for your new site here."}

        {:key :site-subtitle
         :label "Site Subtitle"
         :value "Your awesome title."}

        {:key :contact-email
         :label "Contact Email"
         :value "foo@bar.com"}

        {:key :footer-links
         :label "Footer Links"
         :value (pr-str
                  [{:title "rPub", :url "http://rpub.dev"}
                   {:title "rPub Plugins", :url "https://github.com/rpub-clj/plugins"}])}

        {:key :default-content-type-slug
         :label "Default Content Type Slug"
         :value "pages"}]
       (map #(->setting (assoc % :current-user current-user)))))

(defn initial-plugins [{:keys [current-user] :as _opts}]
  (->> [{:key :rpub.plugins.starter-theme/plugin, :activated true}
        {:key :rpub.plugins.external-editing/plugin, :activated true}
        {:key :rpub.plugins.content-types/plugin, :activated true}
        {:key :rpub.plugins.admin/plugin, :activated true}
        {:key :rpub.plugins.app/plugin, :activated true}]
       (map #(->plugin (assoc % :current-user current-user)))))

(defn seed! [model {:keys [app] :as opts}]
  (let [model' (assoc model :app-id (:id app))]
    (create-app! model' app)
    (create-user! model' (:new-user app))
    (doseq [setting (initial-settings opts)]
      (create-setting! model' setting))
    (doseq [plugin (initial-plugins opts)]
      (update-plugin! model' plugin))))

(ns rpub.model.settings
  (:require [rpub.model.common :as common]))

^:clj-reload/keep
(defprotocol Model
  (-get-settings [model opts])
  (-create-setting! [model setting])
  (-update-setting! [model setting]))

(defn- coerce [model]
  (get-in model [:models :settings] model))

(defn get-settings [model opts]
  (-get-settings (coerce model) opts))

(defn create-setting! [model opts]
  (-create-setting! (coerce model) opts))

(defn update-setting! [model opts]
  (-update-setting! (coerce model) opts))

(defn ->setting [& {:keys [id key label value current-user] :as _opts}]
  (-> {:id (or id (random-uuid))
       :key key
       :value value
       :label label}
      (common/add-metadata current-user)))

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
         :value "Starter"}

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

(defmulti ->model :db-type)

(ns rpub.core
  "The core API for using rPub as a library."
  (:require [babashka.fs :as fs]
            [buddy.core.nonce :as nonce]
            [clojure.tools.logging :as log]
            [clojure.tools.logging.readable :as logr]
            [malli.core :as m]
            [malli.util :as mu]
            [medley.core :as medley]
            [muuntaja.core :as muuntaja]
            [reitit.exception :as exception]
            [reitit.ring :as reitit-ring]
            [reitit.ring.middleware.muuntaja :as reitit-muuntaja]
            [reitit.ring.middleware.parameters :as reitit-parameters]
            [ring.adapter.jetty :as jetty]
            [rpub.admin :as admin]
            [rpub.api :as api]
            [rpub.app :as app]
            [rpub.lib.db :as db]
            [rpub.lib.edn]
            [rpub.lib.html :as html]
            [rpub.lib.permalinks :as permalinks]
            [rpub.lib.transit :as transit]
            [rpub.model :as model]
            [rpub.plugins.content-types.model :as ct-model])
  (:import (org.eclipse.jetty.server Server)))

(defmulti
  plugin
  "A multimethod that returns the plugin definition for key k.

  Each method should return a map with the following keys:

  :label - (required) a label
  :description - (required) a description
  :init - (optional) an initialization function
  :middleware - (optional) a sequence of middleware functions
  :routes - (optional) a sequence of route definitions"
  (fn [k] k))

(defn- ->site-base-url [setting port]
  (or (:value setting) (str "http://localhost:" port)))

(defn- get-registered-plugins []
  (->> (methods plugin)
       (map (fn [[k f]] [k (f k)]))
       (into {})))

(defn- wrap-rpub [handler {:keys [model] :as opts}]
  (fn [{:keys [port] :as req}]
    (let [settings (->> (model/get-settings model {})
                        (medley/index-by :key))
          site-base-url (->site-base-url (:site-base-url settings) port)
          permalink-single (get-in settings [:permalink-single :value])
          permalink-router (if (seq permalink-single)
                             (permalinks/->permalink-router {:single permalink-single})
                             (permalinks/default-permalink-router))
          plugins (model/->plugins (get-registered-plugins) opts)
          req' (merge req opts {:site-base-url site-base-url
                                :permalink-router permalink-router
                                :settings settings
                                :plugins plugins
                                :themes []})]
      (handler req'))))

(defn- error-response [_]
  {:status 500
   :body
   (admin/layout
     {:title "Error"
      :content [:div.p-8.text-center.max-w-4xl.mx-auto
                [:div.text-5xl.font-app-serif.italic.mb-8.mt-16
                 "Internal Server Error"]
                [:div.text-gray-600
                 [:p "Something unexpected happened and we couldn't finish loading the page."]]]})})

(defn- wrap-error-page [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable e
        (log/error e)
        (error-response req)))))

(defn- handle-conflicts [conflicts]
  (log/debug (exception/format-exception :path-conflicts nil conflicts)))

(defn- wrap-plugin-activated [handler plugin-key]
  (fn [{:keys [plugins] :as req}]
    (let [plugin (medley/find-first #(= (:key %) plugin-key) plugins)]
      (if (:activated plugin)
        (handler req)
        (html/not-found req)))))

(defn- plugin-routes [opts]
  (->> (:plugins opts)
       (filter :routes)
       (map (fn [plugin]
              ["" {:middleware [[wrap-plugin-activated (:key plugin)]]}
               ((:routes plugin) opts)]))))

(defmulti ^:private init-db :db-type)

(defmethod init-db :sqlite [_]
  (require 'rpub.model.sqlite))

(defn- init-model [{:keys [db-type ds] :as opts}]
  (init-db opts)
  (let [model (model/->model {:db-type db-type :ds ds})]
    model))

(defn- init-plugins [opts]
  (let [plugins (model/->plugins (get-registered-plugins) opts)
        opts' (merge opts {:plugins plugins
                           :current-user admin/system-user})]
    (doseq [init (keep :init plugins)]
      (init opts'))
    plugins))

(defn- get-config [{:keys [model]}]
  (let [settings (model/get-settings model {:keys [:encrypted-session-store-key]})]
    (->> settings
         (map (fn [setting] [(:key setting) (:value setting)]))
         (into {}))))

(defn- init-opts [opts]
  (as-> opts $
    (assoc $ :model (init-model $))
    (do (model/migrate! (:model $) {:current-user admin/system-user}) $)
    (assoc $ :config (get-config $))
    (assoc $ :plugins (init-plugins $))))

(def SettingMap
  "Malli schema for a setting."
  [:map
   [:key :keyword]
   [:value :string]])

(defn get-settings
  "Get a sequence of settings."
  [model opts]
  (model/get-settings model opts))

(m/=> get-settings
      [:=> [:catn
            [:model [:fn #(satisfies? model/Model %)]]
            [:opts [:map
                    [:keys {:optional true}
                     [:seqable (mu/get SettingMap :key)]]]]]
       [:seqable #'SettingMap]])

(def UserMap
  "Malli schema for a user."
  [:map
   [:id :uuid]
   [:username :string]
   [:password-hash :string]])

(defn get-users
  "Get a sequence of users."
  [model opts]
  (model/get-users model opts))

(m/=> get-users
      [:=> [:catn
            [:model [:fn #(satisfies? model/Model %)]]
            [:opts [:map
                    [:ids {:optional true}
                     [:seqable (mu/get UserMap :id)]]
                    [:usernames {:optional true}
                     [:seqable (mu/get UserMap :username)]]
                    [:password {:optional true}
                     :boolean]]]]
       [:seqable #'UserMap]])

(defn url-for
  "Get a URL for a content item."
  [content-item req]
  (app/url-for content-item req))

(def ContentTypeMap
  "Malli schema for a content type."
  [:map
   [:id :uuid]
   [:slug :keyword]
   [:fields {:optional true} [:seqable [:map]]]])

(defn get-content-types
  "Get a sequence of content types."
  [model opts]
  (let [content-types-model (or (:content-types-model model) model)]
    (ct-model/get-content-types content-types-model opts)))

(m/=> get-content-types
      [:=> [:catn
            [:model [:fn #(satisfies? model/Model %)]]
            [:opts [:map
                    [:content-type-ids {:optional true}
                     [:seqable (mu/get ContentTypeMap :id)]]
                    [:content-type-slugs {:optional true}
                     [:seqable (mu/get ContentTypeMap :slug)]]
                    [:count-items {:optional true}
                     :boolean]]]]
       [:seqable #'ContentTypeMap]])

(def ContentItemMap
  "Malli schema for a content item."
  [:map
   [:id :uuid]
   [:fields [:map-of :string :any]]])

(defn get-content-items
  "Get a sequence of content items."
  [model opts]
  (let [content-types-model (or (:content-types-model model) model)]
    (ct-model/get-content-items content-types-model opts)))

(m/=> get-content-items
      [:=> [:catn
            [:model [:fn #(satisfies? model/Model %)]]
            [:opts [:map
                    [:content-type-ids {:optional true}
                     [:seqable (mu/get ContentTypeMap :id)]]
                    [:content-type-slugs {:optional true}
                     [:seqable (mu/get ContentTypeMap :slug)]]
                    [:content-item-ids {:optional true}
                     [:seqable (mu/get ContentItemMap :id)]]
                    [:content-item-slugs {:optional true}
                     [:seqable :string]]]]]
       [:seqable #'ContentItemMap]])

(defn content-item
  "Returns a content item map from the params."
  [params]
  (ct-model/->content-item params))

(defn create-content-item!
  "Create a content item."
  [model opts]
  (let [content-types-model (or (:content-types-model model) model)]
    (ct-model/create-content-item! content-types-model opts)))

(defn update-content-item!
  "Update a content item."
  [model opts]
  (let [content-types-model (or (:content-types-model model) model)]
    (ct-model/update-content-item! content-types-model opts)))

(defn delete-content-item!
  "Delete a content item."
  [model opts]
  (let [content-types-model (or (:content-types-model model) model)]
    (ct-model/delete-content-item! content-types-model opts)))

(def ^:private custom-muuntaja
  (muuntaja/create
    (-> muuntaja/default-options
        (update-in [:formats "application/transit+json" :decoder-opts]
                   #(update % :handlers merge transit/read-handlers)))))

(defn- ->app-handler [opts]
  (let [opts' (init-opts opts)]
    (reitit-ring/ring-handler
      (reitit-ring/router
        (concat (admin/routes opts')
                (api/routes opts')
                (plugin-routes opts')
                (app/routes opts'))
        {:conflicts handle-conflicts
         :data {:muuntaja custom-muuntaja
                :middleware [reitit-parameters/parameters-middleware
                             reitit-muuntaja/format-middleware
                             [wrap-rpub opts']
                             db/wrap-db-transaction]}})
      (reitit-ring/routes
        (reitit-ring/redirect-trailing-slash-handler {:method :strip})
        (reitit-ring/create-default-handler {:not-found html/not-found})))))

(defn- wrap-exceptions [handler]
  ((requiring-resolve 'prone.middleware/wrap-exceptions)
   handler
   {:app-namespaces ['rpub (ns-name *ns*)]}))

(defn- wrap-setup [handler opts]
  (let [model (init-model opts)]
    (fn [req]
      (let [req' (merge req opts {:model model})]
        (handler req')))))

(defn- ->session-store-key []
  (nonce/random-bytes 16))

(defn- ->setup-handler [opts]
  (let [opts' (assoc opts :session-store-key (->session-store-key))
        not-found-opts {:redirect-to "/admin/setup"}]
    (reitit-ring/ring-handler
      (reitit-ring/router
        [["" (admin/setup-routes opts')]
         ["*path" {:get (constantly nil)
                   :middleware [[app/wrap-default-handlers not-found-opts]]}]]
        {:conflicts handle-conflicts
         :data {:muuntaja custom-muuntaja
                :middleware [reitit-parameters/parameters-middleware
                             reitit-muuntaja/format-middleware
                             [wrap-setup opts']]}})
      (reitit-ring/routes
        (reitit-ring/redirect-trailing-slash-handler {:method :strip})
        (reitit-ring/create-default-handler
          {:not-found (fn [_] (html/not-found not-found-opts))})))))

(defn- db-exists? [database-url]
  (let [[_ db-path] (re-matches #"^jdbc:sqlite:(.+)$" database-url)]
    (fs/exists? db-path)))

(defn- start-jetty! [{:keys [reload port error-page database-url] :as opts}]
  (let [current-handler (atom nil)
        setup-finished (fn []
                         (let [app-handler (->app-handler opts)]
                           (reset! current-handler app-handler)))
        setup-required (fn []
                         (let [opts' (assoc opts :setup-finished setup-finished)
                               setup-handler (->setup-handler opts')]
                           (reset! current-handler setup-handler)))
        handler (cond-> (if reload
                          (reitit-ring/reloading-ring-handler
                            (fn [] @current-handler))
                          (fn [req] (@current-handler req)))
                  error-page wrap-error-page
                  (not error-page) wrap-exceptions)]
    (if (db-exists? database-url)
      (setup-finished)
      (setup-required))
    (jetty/run-jetty handler {:port port, :join? false})))

(defn- start-app! [opts]
  {:server (start-jetty! opts)})

(def defaults
  "The default options for the rPub server."
  {:content-security-policy true
   :database-url "jdbc:sqlite:data/app.db"
   :error-page true
   :port 3000
   :reload false
   :secret-key-file "data/secret.key"})

(defn- stop-app! [app]
  (when-let [{:keys [server]} app]
    (.stop ^Server server)))

(defn start!
  "Start the rPub server."
  [& {:as opts}]
  (let [opts' (merge defaults opts)
        _ (logr/info 'start! opts')
        opts'' (merge opts' {:ds (db/get-datasource (:database-url opts'))
                             :db-type (db/db-type (:database-url opts'))})]
    (start-app! opts'')))

(defn stop!
  "Stop the rPub server."
  [system]
  (stop-app! (:app system)))

(comment
  @model/*registered-plugins*)

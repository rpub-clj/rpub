(ns rpub.core
  "The core API for using rPub as a library."
  (:require [babashka.fs :as fs]
            [buddy.core.nonce :as nonce]
            [malli.core :as m]
            [malli.util :as mu]
            [medley.core :as medley]
            [muuntaja.core :as muuntaja]
            [reitit.ring :as reitit-ring]
            [reitit.ring.middleware.muuntaja :as reitit-muuntaja]
            [reitit.ring.middleware.parameters :as reitit-parameters]
            [ring.adapter.jetty :as jetty]
            [rpub.lib.db :as db]
            [rpub.lib.edn]
            [rpub.lib.html :as html]
            [rpub.lib.otel :as otel]
            [rpub.lib.permalinks :as permalinks]
            [rpub.lib.router :as rpub-router]
            [rpub.lib.transit :as transit]
            [rpub.model :as model]
            [rpub.model.apps :as apps]
            [rpub.model.content-types :as ct-model]
            [rpub.model.plugins :as plugins]
            [rpub.model.settings :as settings]
            [rpub.model.users :as users]
            [taoensso.telemere :as tel])
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

(defn- get-current-app [{:keys [model headers server-name] :as _req}]
  (let [domain (get headers "x-forwarded-host" server-name)]
    (first (apps/get-apps model {:domains [domain]}))))

(defn- wrap-rpub [handler {:keys [model] :as opts}]
  (fn wrap-rpub-handler [{:keys [port] :as req}]
    (let [current-app (get-current-app (assoc req :model model))
          model' (model/add-app-id model (:id current-app))
          opts' (assoc opts :model model')
          settings (->> (settings/get-settings model' {})
                        (medley/index-by :key))
          site-base-url (->site-base-url (:site-base-url settings) port)
          permalink-single (get-in settings [:permalink-single :value])
          permalink-router (if (seq permalink-single)
                             (permalinks/->permalink-router {:single permalink-single})
                             (permalinks/default-permalink-router))
          plugins (plugins/->plugins (get-registered-plugins) opts')
          req' (merge req opts' {:site-base-url site-base-url
                                 :permalink-router permalink-router
                                 :settings settings
                                 :plugins plugins
                                 :themes []})]
      (handler req'))))

(defn- error-page-layout [req current-page]
  ((:error-page-layout req) req current-page))

(defn- error-response [req]
  {:status 500
   :body
   (error-page-layout
     req
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
        (tel/error! e)
        (error-response req)))))

(defmulti ^:private init-db :db-type)

(defmethod init-db :sqlite [_]
  (require 'rpub.model.sqlite))

(defn- init-model [{:keys [db-type ds] :as opts}]
  (init-db opts)
  (let [model (model/->model {:db-type db-type :ds ds})]
    model))

(defn- init-plugins [{:keys [model] :as opts}]
  (let [[app] (apps/get-apps (:model opts) {})
        model' (model/add-app-id model (:id app))
        opts' (assoc opts :model model')
        plugins (plugins/->plugins (get-registered-plugins) opts')
        opts'' (merge opts' {:plugins plugins
                             :current-user users/system-user})]
    (doseq [init (keep :init plugins)]
      (init opts''))
    plugins))

(defn- get-config [{:keys [model]}]
  (let [[app] (apps/get-apps model {})
        model' (model/add-app-id model (:id app))
        settings (settings/get-settings model' {:keys [:encrypted-session-store-key]})]
    (->> settings
         (map (fn [setting] [(:key setting) (:value setting)]))
         (into {}))))

(defn- init-opts [opts]
  (as-> opts $
    (assoc $ :model (init-model $))
    (do (model/migrate! (:model $) {:current-user users/system-user}) $)
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
  (settings/get-settings model opts))

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
   [:password-hash {:optional true} :string]])

(defn get-users
  "Get a sequence of users."
  [model opts]
  (users/get-users model opts))

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
  ((:url-for req) content-item req))

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
    (rpub-router/ring-handler
      (merge opts' {:muuntaja custom-muuntaja
                    :handler-middleware (if (:otel opts')
                                          [[otel/wrap-server-span {:create-span? true}]
                                           otel/wrap-exception-event]
                                          [])
                    :route-middleware [reitit-parameters/parameters-middleware
                                       reitit-muuntaja/format-middleware
                                       [wrap-rpub opts']
                                       db/wrap-db-transaction]
                    :not-found #'html/not-found}))))

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
  (let [opts' (assoc opts :session-store-key (->session-store-key))]
    (reitit-ring/ring-handler
      (reitit-ring/router
        ((:setup-routes opts') opts')
        {:data {:muuntaja custom-muuntaja
                :middleware [reitit-parameters/parameters-middleware
                             reitit-muuntaja/format-middleware
                             [wrap-setup opts']]}})
      (reitit-ring/routes
        (reitit-ring/create-resource-handler {:path "/"})
        (reitit-ring/redirect-trailing-slash-handler {:method :strip})
        (reitit-ring/create-default-handler
          {:not-found (fn [_]
                        (html/not-found {:redirect-to "/admin/setup"}))})))))

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

(defn- default-url-for [& args]
  (apply (requiring-resolve 'rpub.plugins.app.helpers/url-for) args))

(defn- default-error-page-layout [& args]
  (apply (requiring-resolve 'rpub.plugins.admin.helpers/layout) args))

(defn- default-setup-routes [& args]
  (apply (requiring-resolve 'rpub.plugins.admin.setup/setup-routes) args))

(def defaults
  "The default options for the rPub server."
  {:admin-dev false
   :content-security-policy true
   :database-url "jdbc:sqlite:data/app.db"
   :error-page true
   :http-tracing-enabled false
   :otel false
   :port 3000
   :reload false
   :secret-key-file "data/secret.key"
   :url-for default-url-for
   :error-page-layout default-error-page-layout
   :setup-routes default-setup-routes})

(defn- stop-app! [app]
  (when-let [{:keys [server]} app]
    (.stop ^Server server)))

(defn start!
  "Start the rPub server."
  [& {:as opts}]
  (let [opts' (merge defaults opts)
        _ (tel/event! `start! {:data opts'})
        opts'' (merge opts' {:ds (db/get-datasource (:database-url opts'))
                             :db-type (db/db-type (:database-url opts'))})]
    (start-app! opts'')))

(defn stop!
  "Stop the rPub server."
  [system]
  (stop-app! (:app system)))

(comment
  @model/*registered-plugins*)

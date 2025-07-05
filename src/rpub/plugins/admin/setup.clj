(ns rpub.plugins.admin.setup
  (:require [buddy.core.codecs :as codecs]
            [hiccup2.core :as hiccup]
            [ring.middleware.defaults :as defaults]
            [ring.util.response :as response]
            [rpub.lib.ring :as ring]
            [rpub.lib.secrets :as secrets]
            [rpub.model :as model]
            [rpub.model.content-types :as ct-model]
            [rpub.plugins.admin.helpers :as admin-helpers]
            [rpub.plugins.content-types :as content-types]))

(defn- setup-form [_]
  [:section.bg-gray-50
   [:div {:class "flex flex-col items-center justify-center px-6 py-8 mx-auto lg:py-0"}
    (admin-helpers/logo {:class "mb-8 text-6xl"})
    [:div {:class "w-full p-6 bg-white rounded-lg shadow-sm md:mt-0 max-w-md sm:p-8"}
     [:h2
      {:class "mb-1 text-2xl font-semibold font-app-serif leading-tight tracking-tight text-gray-900 md:text-2xl"}
      "Set Up"]
     (admin-helpers/form
       {:action "/admin/setup"
        :class "mt-4 space-y-4 lg:mt-5 md:space-y-5"}
       [:div
        [:input#username
         {:class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
          :type "text" :name "username" :placeholder "Username" :required ""}]]
       [:div
        [:input#password
         {:class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
          :type "password" :name "password" :placeholder "Password" :required ""}]]
       [:div
        [:input#site-title
         {:class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
          :type "text" :name "site-title" :placeholder "Site Title" :required ""}]]
       [:div
        [:input#site-base-url
         {:class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
          :type "text" :name "site-base-url" :placeholder "Site URL" :required ""}]
        [:script (hiccup/raw "document.querySelector('#site-base-url').value = window.location.origin;")]]
       [:button {:class "w-full text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2 5 text-center"
                 :type "submit"}
        "Continue"])]]])

(defn- setup-start-handler [req]
  (admin-helpers/page-response
    req
    {:cljs false
     :title "rPub Setup"
     :primary (setup-form req)}))

(defn- encrypt-session-store-key [session-store-key secret-key]
  (-> session-store-key
      codecs/bytes->hex
      (secrets/encrypt secret-key)))

(defn- setup-finish-handler
  [{:keys [form-params
           model
           setup-finished
           session
           secret-key-file
           session-store-key]
    :as _req}]
  (let [secret-key (secrets/init-secret-key secret-key-file)
        config {:encrypted-session-store-key (encrypt-session-store-key
                                               session-store-key
                                               secret-key)}
        app-id (random-uuid)
        current-user (assoc model/system-user :app-id app-id)
        new-user (model/->user {:username (get form-params "username")
                                :password (get form-params "password")
                                :current-user current-user})
        app (model/->app {:id app-id
                          :new-user new-user
                          :domains ["localhost"]
                          :current-user current-user})
        session' (assoc session :identity (select-keys new-user [:id]))
        site-title (get form-params "site-title")
        site-base-url (get form-params "site-base-url")
        seed-opts {:current-user current-user
                   :app app
                   :config config
                   :site-title site-title
                   :site-base-url site-base-url}
        _ (content-types/init {:model model, :current-user current-user})
        ct-model (ct-model/->model (merge (model/db-info model)
                                          {:current-user current-user}))]
    (model/migrate! model seed-opts)
    (model/seed! model seed-opts)
    (content-types/seed! ct-model)
    (setup-finished)
    (-> (response/redirect "/admin")
        (assoc :session session'))))

(defn setup-middleware [opts]
  (let [opts' (merge opts {:session false, :auth-required false})]
    [[defaults/wrap-defaults (ring/site-defaults opts')]
     ring/wrap-no-cache]))

(defn setup-routes [opts]
  ["/admin/setup" {:middleware (setup-middleware opts)
                   :get setup-start-handler
                   :post setup-finish-handler}])

(ns rpub.admin
  {:no-doc true}
  (:require [babashka.fs :as fs]
            [buddy.core.codecs :as codecs]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup2.core :as hiccup]
            [medley.core :as medley]
            [rads.inflections :as inflections]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.defaults :as defaults]
            [ring.util.response :as response]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]
            [rpub.lib.plugins :as plugins]
            [rpub.lib.ring :as ring]
            [rpub.lib.secrets :as secrets]
            [rpub.lib.tap :as tap]
            [rpub.model :as model]
            [rpub.plugins.content-types :as content-types]))

(defn page-response [req current-page]
  (admin-impl/page-response req current-page))

(defn admin-middleware [opts]
  (admin-impl/admin-middleware opts))

(defn layout [req]
  (admin-impl/layout req))

(def system-user
  {:id #uuid"00000000-0000-0000-0000-000000000000"})

(defn- form [& args]
  (let [[attrs content] (if (map? (first args))
                          [(first args) (rest args)]
                          [nil args])]
    [:form (merge {:method "post"} attrs)
     [:input {:id :__anti-forgery-token
              :name :__anti-forgery-token
              :type :hidden
              :value (force anti-forgery/*anti-forgery-token*)}]
     content]))

(defn- redirect-field [{:keys [redirect-to] :as _flash}]
  (when redirect-to
    [:input {:name :redirect-to
             :type :hidden
             :value redirect-to}]))

(defn- get-version-from-deps-edn []
  (let [deps-file (fs/file "deps.edn")]
    (when (fs/exists? deps-file)
      (let [deps-edn (edn/read-string (slurp deps-file))]
        (get-in deps-edn [:aliases :neil :project :version])))))

(defn- get-version-from-pom []
  (when-let [pom (io/resource "META-INF/maven/dev.rpub/rpub/pom.xml")]
    (let [lines (str/split-lines (slurp pom))
          [_ version] (some #(re-matches #" +<version>(.+)</version>" %) lines)]
      version)))

(def ^:private rpub-version
  (delay
    (or (get-version-from-deps-edn)
        (get-version-from-pom))))

(defn- dashboard-handler [{:keys [current-user plugins settings] :as req}]
  (let [content-types (content-types/get-content-types
                        (::content-types/model req)
                        {:count-items true})
        theme (-> (model/active-theme req) (select-keys [:label]))
        activated-plugins' (->> plugins
                                (remove :rpub/internal)
                                (filter :activated)
                                (map #(select-keys % [:label])))
        current-user' (select-keys current-user [:id :username])
        settings' (-> (select-keys settings [:permalink-single])
                      (update-vals #(select-keys % [:id :label :value])))]
    (admin-impl/page-response
      req
      {:title "Dashboard"
       :primary
       (html/custom-element
         [:dashboard-page
          {:rpub-version @rpub-version
           :content-types content-types
           :theme theme
           :activated-plugins activated-plugins'
           :current-user current-user'
           :settings settings'}])})))

(defn- users-handler [{:keys [model] :as req}]
  (admin-impl/page-response
    req
    {:title "Users"
     :primary
     (let [users (model/get-users model {})]
       (html/custom-element
         [:users-page {:users users}]))}))

(defn- settings-handler [{:keys [model] :as req}]
  (admin-impl/page-response
    req
    {:title "Settings"
     :primary
     (let [settings (model/get-settings
                      model
                      {:keys [:site-title
                              :site-base-url
                              :site-description
                              :site-subtitle
                              :contact-email
                              :footer-links]})]
       (html/custom-element
         [:settings-page {:settings settings}]))}))

(defn- themes-handler [{:keys [model themes] :as req}]
  (let [[theme-name-setting] (model/get-settings model {:keys [:theme-name]})
        themes' (map #(select-keys % [:label :description]) themes)]
    (admin-impl/page-response
      req
      {:title "Themes"
       :primary
       (html/custom-element
         [:themes-page {:theme-name-setting theme-name-setting
                        :themes themes'}])})))

(defn- plugins-handler [{:keys [plugins] :as req}]
  (let [available-plugins (->> (plugins/get-plugins)
                               (map #(select-keys % [:key :label :description])))
        current-plugins (->> plugins
                             (remove :rpub/internal)
                             (map #(select-keys % [:key :label :activated])))]
    (admin-impl/page-response
      req
      {:title "Plugins"
       :primary
       (html/custom-element
         [:plugins-page
          {:current-plugins current-plugins
           :available-plugins available-plugins}])})))

(defn- login-start-handler [{:keys [flash] :as req}]
  (admin-impl/page-response
    req
    {:cljs false
     :title "Log In"
     :primary
     (form
       (redirect-field flash)
       [:div.flex.flex-col.items-center.justify-center.px-6.py-8.mx-auto.lg:py-0
        (admin-impl/logo {:class "mb-8 text-6xl"})
        [:div.w-full.bg-white.rounded-lg.shadow.dark:border.md:mt-0.max-w-md.xl:p-0.dark:bg-gray-800.dark:border-gray-700
         [:div.p-6.space-y-4.md:space-y-6.sm:p-8
          [:form.space-y-4.md:space-y-6
           {:action "#"}
           [:div
            [:input#username.bg-gray-50.border.border-gray-300.text-gray-900.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-blue-500.dark:focus:border-blue-500
             {:type :text :name :username :placeholder "Username" :required ""}]]
           [:div
            [:input#password.bg-gray-50.border.border-gray-300.text-gray-900.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-blue-500.dark:focus:border-blue-500
             {:type :password :name :password :placeholder "Password" :required ""}]]
           [:button.w-full.text-white.bg-blue-600.hover:bg-blue-700.focus:ring-4.focus:outline-none.focus:ring-blue-300.font-medium.rounded-lg.text-sm.px-5.py-2.5.text-center.dark:bg-blue-600.dark:hover:bg-blue-700.dark:focus:ring-blue-800
            {:type "submit"}
            "Sign In"]]]]])}))

(defn- next-url [req]
  (or (get-in req [:form-params "redirect-to"])
      (get-in req [:flash :redirect-to])
      "/admin"))

(defn admin-path? [uri]
  (str/starts-with? uri "/admin"))

(defn- login-finish-handler [{:keys [session model] :as req}]
  (let [username (get-in req [:form-params "username"])
        raw-password (get-in req [:form-params "password"])
        [found-user] (model/get-users model {:usernames [username]
                                             :password true})
        authorized (when found-user
                     (model/verify-password found-user raw-password))]
    (if-not authorized
      (login-start-handler req)
      (let [current-user (select-keys found-user [:id])
            updated-session (assoc session :identity current-user)]
        (-> (response/redirect (next-url req))
            (assoc :session updated-session))))))

(defn- logout-handler [req]
  (-> (response/redirect (next-url req))
      (assoc :session nil)))

(defn- setup-form [_]
  [:section.bg-gray-50.dark:bg-gray-900
   [:div.flex.flex-col.items-center.justify-center.px-6.py-8.mx-auto.lg:py-0
    (admin-impl/logo {:class "mb-8 text-6xl"})
    [:div.w-full.p-6.bg-white.rounded-lg.shadow.dark:border.md:mt-0.max-w-md.dark:bg-gray-800.dark:border-gray-700.sm:p-8
     [:h2.mb-1.text-2xl.font-semibold.font-app-serif.leading-tight.tracking-tight.text-gray-900.md:text-2xl.dark:text-white
      "Set Up"]
     (form
       {:action "/admin/setup"
        :class "mt-4 space-y-4 lg:mt-5 md:space-y-5"}
       [:div
        [:input#username.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-blue-500.dark:focus:border-blue-500
         {:type "text" :name "username" :placeholder "Username" :required ""}]]
       [:div
        [:input#password.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-blue-500.dark:focus:border-blue-500
         {:type "password" :name "password" :placeholder "Password" :required ""}]]
       [:div
        [:input#site-title.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-blue-500.dark:focus:border-blue-500
         {:type "text" :name "site-title" :placeholder "Site Title" :required ""}]]
       [:div
        [:input#site-base-url.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-blue-500.dark:focus:border-blue-500
         {:type "text" :name "site-base-url" :placeholder "Site URL" :required ""}]
        [:script (hiccup/raw "document.querySelector('#site-base-url').value = window.location.origin;")]]
       [:button.w-full.text-white.bg-blue-600.hover:bg-blue-700.focus:ring-4.focus:outline-none.focus:ring-blue-300.font-medium.rounded-lg.text-sm.px-5.py-2.5.text-center.dark:bg-blue-600.dark:hover:bg-blue-700.dark:focus:ring-blue-800
        {:type "submit"}
        "Continue"])]]])

(defn- setup-start-handler [req]
  (admin-impl/page-response
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
        encrypted-session-store-key (encrypt-session-store-key
                                      session-store-key
                                      secret-key)
        new-user (model/->user {:username (get form-params "username")
                                :password (get form-params "password")
                                :current-user system-user})
        current-user (select-keys new-user [:id])
        session' (assoc session :identity current-user)]
    (let [site-title (get form-params "site-title")
          site-base-url (get form-params "site-base-url")]
      (model/migrate! model {:current-user system-user
                             :new-user new-user
                             :encrypted-session-store-key encrypted-session-store-key
                             :site-title site-title
                             :site-base-url site-base-url}))
    (setup-finished)
    (-> (response/redirect "/admin")
        (assoc :session session'))))

(defn setup-middleware [opts]
  (let [opts' (merge opts {:session false, :auth-required false})]
    [[defaults/wrap-defaults (ring/site-defaults opts')]
     ring/wrap-no-cache
     ring/wrap-tap]))

(defn setup-routes [opts]
  ["/admin/setup" {:middleware (setup-middleware opts)
                   :get setup-start-handler
                   :post setup-finish-handler}])

(defn- update-settings-handler
  [{:keys [model current-user body-params] :as _req}]
  (let [updated-setting-index (->> (get body-params :settings)
                                   (map #(select-keys % [:key :value]))
                                   (medley/index-by :key))
        ks (keys updated-setting-index)
        existing-setting-index (->> (model/get-settings model {:keys ks})
                                    (medley/index-by :key))
        combined-setting-index (merge-with merge
                                           existing-setting-index
                                           updated-setting-index)
        to-update (map #(model/add-metadata % current-user)
                       (vals combined-setting-index))]
    (doseq [setting to-update]
      (model/update-setting! model setting))
    (response/response {:success true})))

(defn- last-updated [unsaved-changes]
  (->> unsaved-changes
       (sort-by #(or (:updated-at %) (:created-at %)) #(compare %2 %1))
       first))

(defn- update-unsaved-changes-handler
  [{:keys [model current-user body-params] :as _req}]
  (let [existing-unsaved-changes (-> (model/get-unsaved-changes
                                       model
                                       {:user-ids [(:id current-user)]
                                        :keys [(:key body-params)]})
                                     last-updated)
        updated-unsaved-changes (model/->unsaved-changes
                                  (-> (if existing-unsaved-changes
                                        (assoc existing-unsaved-changes :value (:value body-params))
                                        body-params)
                                      (dissoc :id)
                                      (assoc :current-user current-user)
                                      (assoc :client-id (:client-id body-params))))]
    (model/update-unsaved-changes! model updated-unsaved-changes)
    (response/response {:success true})))

(defn- activate-plugin-handler
  [{:keys [body-params model current-user plugins] :as req}]
  (let [plugin' (model/->plugin (merge (:plugin body-params)
                                       {:activated true
                                        :sha (plugins/get-latest-sha)
                                        :current-user current-user}))
        remote-plugins (plugins/get-plugins)]
    (if-not (plugins/can-activate? plugins remote-plugins plugin')
      (-> (response/status 403)
          (assoc :body {:message "Plugin not in the list of installable plugins"
                        :plugin-label (:label plugin')}))
      (do
        (when (plugins/remote-plugin? remote-plugins plugin')
          (plugins/install! plugin' req))
        (model/update-plugin! model plugin')
        (response/response {:success true})))))

(defn- deactivate-plugin-handler
  [{:keys [body-params model current-user] :as _req}]
  (let [plugin' (model/->plugin (merge (:plugin body-params)
                                       {:activated false
                                        :current-user current-user}))]
    (plugins/uninstall! plugin')
    (model/update-plugin! model plugin')
    (response/response {:success true})))

(defn- restart-server-handler [_]
  (future
    (Thread/sleep 1000)
    (System/exit 0))
  (response/response {:success true}))

(defn update-content-types-handler [_]
  (response/response {:success true}))

(defn dag-metadata-path [storage-id]
  (format "dev/rpub/dev/dag/metadata/%s.edn"
          (inflections/parameterize (str storage-id))))

(defn get-dag-metadata-handler [{:keys [body-params] :as _req}]
  (let [storage-id (get body-params :storage-id)
        saved-nodes (when (.exists (io/file (dag-metadata-path storage-id)))
                      (-> (slurp (dag-metadata-path storage-id))
                          edn/read-string
                          :saved-nodes))]
    (response/response {:saved-nodes saved-nodes})))

(defn- update-dag-metadata-handler [{:keys [body-params] :as _req}]
  (let [storage-id (:key body-params)]
    (spit (dag-metadata-path storage-id) (pr-str (:value body-params)))
    (response/response {:success true})))

(defn routes [opts]
  [["" {:middleware (admin-impl/admin-middleware (assoc opts :tap false))}
    ["/admin/api/get-dag-metadata" {:post #'get-dag-metadata-handler}]
    ["/admin/api/update-dag-metadata" {:post #'update-dag-metadata-handler}]
    ["/admin/api/tap" {:post #'tap/handler}]]
   ["" {:middleware (admin-impl/admin-middleware opts)}
    ["/admin" {:get #'dashboard-handler}]
    ["/admin/api/activate-plugin" {:post #'activate-plugin-handler}]
    ["/admin/api/deactivate-plugin" {:post #'deactivate-plugin-handler}]
    ["/admin/api/restart-server" {:post #'restart-server-handler}]
    ["/admin/api/update-content-types" {:post #'update-content-types-handler}]
    ["/admin/api/update-settings" {:post #'update-settings-handler}]
    ["/admin/api/update-unsaved-changes" {:post #'update-unsaved-changes-handler}]
    ["/admin/login" {:get #'login-start-handler
                     :post #'login-finish-handler}]
    ["/admin/logout" {:post #'logout-handler}]
    ["/admin/plugins" {:get #'plugins-handler}]
    ["/admin/settings" {:get #'settings-handler}]
    ["/admin/themes" {:get #'themes-handler}]
    ["/admin/users" {:get #'users-handler}]]])

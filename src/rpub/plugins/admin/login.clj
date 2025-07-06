(ns rpub.plugins.admin.login
  {:no-doc true}
  (:require [ring.util.response :as response]
            [rpub.model.users :as users]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- redirect-field [{:keys [redirect-to] :as _flash}]
  (when redirect-to
    [:input {:name :redirect-to
             :type :hidden
             :value redirect-to}]))

(defn- login-start-handler [{:keys [flash] :as req}]
  (helpers/page-response
    req
    {:cljs false
     :title "Log In"
     :primary
     (helpers/form
       (redirect-field flash)
       [:div {:class "flex flex-col items-center justify-center px-6 py-8 mx-auto lg:py-0"}
        (helpers/logo {:class "mb-8 text-6xl"})
        [:div {:class "w-full bg-white rounded-lg shadow-sm md:mt-0 max-w-md xl:p-0"}
         [:div {:class "p-6 space-y-4 md:space-y-6 sm:p-8"}
          [:form {:class "space-y-4 md:space-y-6" :action "#"}
           [:div
            [:input#username {:class "bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
                              :type :text :name :username :placeholder "Username" :required ""}]]
           [:div
            [:input#password {:class "bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
                              :type :password :name :password :placeholder "Password" :required ""}]]
           [:button {:class "w-full text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
                     :type "submit"}
            "Sign In"]]]]])}))

(defn- next-url [req]
  (or (get-in req [:form-params "redirect-to"])
      (get-in req [:flash :redirect-to])
      "/admin"))

(defn- login-finish-handler [{:keys [session model] :as req}]
  (let [username (get-in req [:form-params "username"])
        raw-password (get-in req [:form-params "password"])
        [found-user] (users/get-users model {:usernames [username]
                                             :password true})
        authorized (when found-user
                     (users/verify-password found-user raw-password))]
    (if-not authorized
      (login-start-handler req)
      (let [current-user (select-keys found-user [:id])
            updated-session (assoc session :identity current-user)]
        (-> (response/redirect (next-url req))
            (assoc :session updated-session))))))

(defn- logout-handler [req]
  (-> (response/redirect (next-url req))
      (assoc :session nil)))

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin/login" {:get #'login-start-handler
                     :post #'login-finish-handler}]
    ["/admin/logout" {:post #'logout-handler}]]])

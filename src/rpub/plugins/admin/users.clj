(ns rpub.plugins.admin.users
  {:no-doc true}
  (:require [ring.util.response :as response]
            [rpub.core :as rpub]
            [rpub.lib.html :as html]
            [rpub.model.users :as users]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.admin.roles :as roles]))

(defn- users-handler [{:keys [model current-user] :as req}]
  (let [roles (users/get-roles model {})
        users (users/get-users model {:roles true})
        content-types (rpub/get-content-types model {})
        permissions (roles/all-permissions {:roles roles
                                            :content-types content-types})]
    (helpers/page-response
      req
      {:title "Users"
       :primary
       (html/custom-element
         [:all-users-page {:current-user (select-keys current-user [:id :roles])
                           :roles roles
                           :users users
                           :permissions permissions}])})))

(defn- new-user-handler [{:keys [current-user] :as req}]
  (roles/assert-allowed current-user {:resource :users, :action :create})
  (helpers/page-response
    req
    {:title "New User"
     :primary
     (html/custom-element
       [:single-user-page {}])}))

(defn- create-user-handler [{:keys [model current-user body-params] :as _req}]
  (roles/assert-allowed current-user {:resource :users, :action :create})
  (let [{:keys [username password]} body-params
        user (users/->user :username username
                           :password password
                           :current-user current-user)]
    (users/create-user! model user)
    (response/redirect "/admin/users")))

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin/api/create-user" {:post #'create-user-handler}]
    ["/admin/users" {:get #'users-handler}]
    ["/admin/users/new" {:get #'new-user-handler}]]])

(ns rpub.plugins.admin.users-page
  (:require [rads.inflections :as inflections]
            [rpub.lib.html :as html]
            [rpub.plugins.admin.impl :as admin-impl]))

(def ^:private users-columns
  [{:name "Username"
    :value (fn [{:keys [username]}]
             [:a {:class "font-semibold"} username])}
   {:name "Roles"
    :value (fn [_]
             "Admin")}])

(defn- checkbox [{:keys [checked disabled]}]
  [:input {:type "checkbox"
           :checked checked
           :disabled disabled
           :class "w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"}])

(def ^:private permissions-columns
  [{:name "Permission"
    :value (fn [{:keys [permission-key]}]
             (inflections/titleize (name permission-key)))}
   {:name "Admin"
    :value (fn [{:keys [roles]}]
             (let [checked (contains? roles :admin)]
               [checkbox {:checked checked, :disabled true}]))}
   {:name "Contributor"
    :value (fn [{:keys [roles]}]
             (let [checked (contains? roles :contributor)]
               [checkbox {:checked checked}]))}])

(def ^:private default-roles
  [{:id (uuid "b84752e4-de2a-4329-a315-cac6a5c97b0b")
    :label "Admin"
    :permissions {:create-page true
                  :edit-page true
                  :create-post true
                  :edit-post true}}
   {:id (uuid "f9846fb4-1832-46f3-afe2-80670d5a6bdc")
    :label "Contributor"
    :permissions {:create-page true
                  :edit-page true
                  :create-post true
                  :edit-post true}}])

(defn roles->permissions [_]
  [{:permission-key :create-user, :roles #{:admin}}
   {:permission-key :edit-user, :roles #{:admin}}
   {:permission-key :create-post, :roles #{:admin :contributor}}
   {:permission-key :edit-post, :roles #{:admin :contributor}}])

(defn- page [{:keys [users]}]
  [:div {:class "p-4"}
   [:div {:class "mb-4"}
    [admin-impl/table
     {:title "Users"
      :header-buttons [:a {:href "/admin/users/new"}
                       [html/action-button "New User"]]
      :columns users-columns
      :rows users}]]
   [:div
    [admin-impl/table
     {:title "Roles & Permissions"
      :header-buttons [:a {:href "/admin/users/new"}
                       [html/action-button "New Role"]]
      :columns permissions-columns
      :rows (roles->permissions default-roles)}]]])

(def config
  {:page-id :users-page
   :component page})

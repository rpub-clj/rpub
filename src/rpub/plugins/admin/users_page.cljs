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

(defn- page [{:keys [users roles permissions]}]
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
      :description [:div
                    (for [role roles]
                      [:div {:class "mb-4"}
                       [:div {:class "inline-block font-semibold text-lg"
                              :style "min-width: 150px"}
                        (:label role)]
                       [:select {:class "inline-block"}
                        [:option "Access to all permissions"]
                        [:option "Access to manually selected permissions"]]])]
      :header-buttons [:a {:href "/admin/users/new"}
                       [html/action-button "New Role"]]
      :columns permissions-columns
      :rows permissions}]]])

(def config
  {:page-id :users-page
   :component page})

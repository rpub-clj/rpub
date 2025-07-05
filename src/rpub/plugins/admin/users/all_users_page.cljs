(ns rpub.plugins.admin.users.all-users-page
  (:require [clojure.string :as str]
            [rads.inflections :as inflections]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.admin.roles :as roles]))

(def ^:private users-columns
  [{:name "Username"
    :value (fn [{:keys [username]}]
             [:a {:class "font-semibold"} username])}
   {:name "Roles"
    :value (fn [{:keys [roles]}]
             (str/join ", " (map :label roles)))}])

(defn- checkbox [{:keys [checked disabled on-change]}]
  [:input {:type "checkbox"
           :checked checked
           :disabled disabled
           :on-change on-change
           :class "w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"}])

(def ^:private permission-column
  {:name "Permission"
   :value (fn [{:keys [permission-key]}]
            (inflections/titleize (name permission-key)))})

(defn- role-column [{:keys [current-user role]}]
  {:name (:label role)
   :value (fn [row]
            (let [checked (contains? (set (:roles row)) (:label role))
                  disabled (or (= (:label role) "Admin")
                               (not (roles/allowed? current-user (:permission row))))
                  handle-change (fn [e]
                                  (let [checked (-> e .-target .-checked)]
                                    (http/post "/admin/api/update-role"
                                               {:permission (:permission row)
                                                :allowed checked})))]
              [checkbox {:checked checked
                         :disabled disabled
                         :on-change handle-change}]))})

(defn- permissions-columns [{:keys [current-user roles]}]
  (concat [permission-column]
          (map #(role-column {:role %, :current-user current-user})
               roles)))

(defn- page [{:keys [current-user users roles permissions]}]
  [:div {:class "p-4"}
   [:div {:class "mb-4"}
    [helpers/table
     {:title "Users"
      :header-buttons (when (roles/allowed? current-user {:resource :users
                                                          :action :create})
                        [:a {:href "/admin/users/new"}
                         [html/action-button "New User"]])
      :columns users-columns
      :rows users}]]
   [:div
    [helpers/table
     {:title "Roles & Permissions"
      :description [:div
                    (for [role roles]
                      [:div {:class "mb-4"}
                       [:div {:class "inline-block font-semibold text-lg"
                              :style "min-width: 150px"}
                        (:label role)]
                       [:select {:class "inline-block"
                                 :value (if (= (:permissions role) :all)
                                          "all"
                                          "manual")
                                 :disabled true}
                        [:option {:value "all"} "Access to all permissions"]
                        [:option {:value "manual"} "Access to manually selected permissions"]]])]
      :header-buttons (when (roles/allowed? current-user {:resource :roles
                                                          :action :create})
                        [html/action-button
                         {:on-click (fn [_e]
                                      (let [role-name (js/prompt "New Role Name:")]
                                        (http/post "/admin/api/create-role"
                                                   {:body {:role-name role-name}})))}
                         "New Role"])
      :columns (permissions-columns
                 {:current-user current-user
                  :roles roles})
      :rows permissions}]]])

(def config
  {:page-id :all-users-page
   :component page})

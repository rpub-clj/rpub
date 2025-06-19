(ns rpub.plugins.admin.users-page
  (:require [rpub.lib.html :as html]
            [rpub.plugins.admin.impl :as admin-impl]))

(def ^:private columns
  [{:name "Username"
    :value (fn [{:keys [username]}]
             [:a {:class "font-semibold"} username])}])

(defn- page [{:keys [users]}]
  [:div {:class "p-4"}
   [admin-impl/table
    {:title "Users"
     :header-buttons [:a {:href "/admin/users/new"}
                      [html/action-button "New User"]]
     :columns columns
     :rows users}]])

(def config
  {:page-id :users-page
   :component page})

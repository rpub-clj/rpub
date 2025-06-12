(ns rpub.admin.users-page
  (:require [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]))

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

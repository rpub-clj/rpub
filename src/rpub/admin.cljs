(ns rpub.admin
  (:require ["preact/devtools"]
            [rpub.admin.dashboard-page :as dashboard-page]
            [rpub.admin.impl :as admin-impl]
            [rpub.admin.new-user-page :as new-user-page]
            [rpub.admin.plugins-page :as plugins-page]
            [rpub.admin.settings-page :as settings-page]
            [rpub.admin.themes-page :as themes-page]
            [rpub.admin.users-page :as users-page]
            [rpub.plugins.content-types.admin :as content-types-admin]))

(defn- add-pages [opts]
  (admin-impl/add-page (merge opts dashboard-page/config))
  (admin-impl/add-page (merge opts settings-page/config))
  (admin-impl/add-page (merge opts users-page/config))
  (admin-impl/add-page (merge opts new-user-page/config))
  (admin-impl/add-page (merge opts themes-page/config))
  (admin-impl/add-page (merge opts plugins-page/config)))

(defn start! [& {:as opts}]
  (add-pages opts)
  (content-types-admin/add-elements opts)
  (content-types-admin/add-pages opts))

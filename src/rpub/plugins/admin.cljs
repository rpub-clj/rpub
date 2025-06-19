(ns rpub.plugins.admin
  (:require ["preact/devtools"]
            [rpub.plugins.admin.dashboard-page :as dashboard-page]
            [rpub.plugins.admin.helpers :as admin-helpers]
            [rpub.plugins.admin.new-user-page :as new-user-page]
            [rpub.plugins.admin.plugins-page :as plugins-page]
            [rpub.plugins.admin.settings-page :as settings-page]
            [rpub.plugins.admin.themes-page :as themes-page]
            [rpub.plugins.admin.users-page :as users-page]
            [rpub.plugins.content-types.admin :as content-types-admin]))

(defn- add-pages [opts]
  (admin-helpers/add-page (merge opts dashboard-page/config))
  (admin-helpers/add-page (merge opts settings-page/config))
  (admin-helpers/add-page (merge opts users-page/config))
  (admin-helpers/add-page (merge opts new-user-page/config))
  (admin-helpers/add-page (merge opts themes-page/config))
  (admin-helpers/add-page (merge opts plugins-page/config)))

(defn start! [& {:as opts}]
  (add-pages opts)
  (content-types-admin/add-elements opts)
  (content-types-admin/add-pages opts))

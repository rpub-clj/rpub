(ns rpub.plugins.admin
  (:require ["preact/devtools"]
            [rpub.plugins.admin.all-themes-page :as all-themes-page]
            [rpub.plugins.admin.dashboard-page :as dashboard-page]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.admin.new-user-page :as new-user-page]
            [rpub.plugins.admin.plugins-page :as plugins-page]
            [rpub.plugins.admin.settings-page :as settings-page]
            [rpub.plugins.admin.single-theme-page :as single-theme-page]
            [rpub.plugins.admin.users-page :as users-page]
            [rpub.plugins.content-types.admin :as content-types-admin]))

(defn- add-pages [opts]
  (helpers/add-page (merge opts dashboard-page/config))
  (helpers/add-page (merge opts settings-page/config))
  (helpers/add-page (merge opts users-page/config))
  (helpers/add-page (merge opts new-user-page/config))
  (helpers/add-page (merge opts all-themes-page/config))
  (helpers/add-page (merge opts single-theme-page/config))
  (helpers/add-page (merge opts plugins-page/config)))

(defn start! [& {:as opts}]
  (add-pages opts)
  (content-types-admin/add-elements opts)
  (content-types-admin/add-pages opts))

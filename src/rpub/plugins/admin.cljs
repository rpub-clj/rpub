(ns rpub.plugins.admin
  (:require ["preact/devtools"]
            [rpub.lib.forms :as forms]
            [rpub.lib.substrate :as subs]
            [rpub.lib.substrate.react :as subs-react]
            [rpub.plugins.admin.dashboard.page :as dashboard-page]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.admin.plugins.page :as plugins-page]
            [rpub.plugins.admin.settings.page :as settings-page]
            [rpub.plugins.admin.themes.all-themes-page :as all-themes-page]
            [rpub.plugins.admin.themes.single-theme-page :as single-theme-page]
            [rpub.plugins.admin.users.all-users-page :as all-users-page]
            [rpub.plugins.admin.users.single-user-page :as single-user-page]
            [rpub.plugins.content-types.admin :as content-types-admin]))

(defn- add-pages [opts]
  (helpers/add-page (merge opts dashboard-page/config))
  (helpers/add-page (merge opts settings-page/config))
  (helpers/add-page (merge opts all-users-page/config))
  (helpers/add-page (merge opts single-user-page/config))
  (helpers/add-page (merge opts all-themes-page/config))
  (helpers/add-page (merge opts single-theme-page/config))
  (helpers/add-page (merge opts plugins-page/config)))

(def conn (atom {}))

(def model
  (merge-with
    merge
    forms/model
    (:model all-themes-page/config)
    (:model plugins-page/config)
    content-types-admin/model))

(defn substrate []
  (subs-react/substrate {:conn conn, :model model}))

(defn start! [& {:as opts}]
  (subs/set-global! (substrate))
  (add-pages opts)
  (content-types-admin/add-elements opts)
  (content-types-admin/add-pages opts))

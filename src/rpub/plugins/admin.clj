(ns rpub.plugins.admin
  {:no-doc true}
  (:require [rpub.model.plugins :as plugins-model]
            [rpub.plugins.admin.dashboard :as dashboard]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.admin.login :as login]
            [rpub.plugins.admin.plugins :as plugins]
            [rpub.plugins.admin.settings :as settings]
            [rpub.plugins.admin.themes :as themes]
            [rpub.plugins.admin.unsaved-changes :as unsaved-changes]
            [rpub.plugins.admin.users :as users]))

(defn dev-routes [opts]
  ((requiring-resolve 'rpub.plugins.admin.dev/routes) opts))

(defn routes [{:keys [admin-dev] :as opts}]
  (let [admin-middleware (helpers/admin-middleware opts)
        opts' (assoc opts :admin-middleware admin-middleware)]
    [(unsaved-changes/routes opts')
     (dashboard/routes opts')
     (settings/routes opts')
     (login/routes opts')
     (plugins/routes opts')
     (users/routes opts')
     (themes/routes opts')
     (when admin-dev (dev-routes opts'))]))

(defmethod plugins-model/internal-plugin ::plugin [_]
  {:routes routes})

(ns rpub.plugins.admin
  {:no-doc true}
  (:require [clojure.string :as str]
            [rpub.model :as model]
            [rpub.model.app :as-alias model-app]
            [rpub.plugins.admin.dashboard :as dashboard]
            [rpub.plugins.admin.dev :as dev]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.admin.login :as login]
            [rpub.plugins.admin.plugins :as plugins]
            [rpub.plugins.admin.settings :as settings]
            [rpub.plugins.admin.themes :as themes]
            [rpub.plugins.admin.unsaved-changes :as unsaved-changes]
            [rpub.plugins.admin.users :as users]))

(def system-user model/system-user)

(defn page-response [req current-page]
  (helpers/page-response req current-page))

(defn admin-middleware [opts]
  (helpers/admin-middleware opts))

(defn admin-path? [uri]
  (str/starts-with? uri "/admin"))

(defn routes [opts]
  (let [admin-middleware (helpers/admin-middleware opts)
        opts' (assoc opts :admin-middleware admin-middleware)]
    [(unsaved-changes/routes opts')
     (dashboard/routes opts')
     (settings/routes opts')
     (login/routes opts')
     (plugins/routes opts')
     (users/routes opts')
     (themes/routes opts')
     (dev/routes opts')]))

(defmethod model/internal-plugin ::plugin [_]
  {:routes routes})

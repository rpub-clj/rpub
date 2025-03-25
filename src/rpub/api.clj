(ns rpub.api
  {:no-doc true}
  (:require [buddy.auth.backends :as buddy-backends]
            [ring.middleware.defaults :as defaults]
            [rpub.lib.plugins :as plugins]
            [rpub.lib.ring :as ring]
            [rpub.model :as model]))

(defn api-middleware [opts]
  (let [opts' (merge {:auth-required true} opts)
        {:keys [auth-required]} opts'
        auth-backend (buddy-backends/jws)]
    (concat
      [[defaults/wrap-defaults (ring/api-defaults opts')]]
      (when auth-required
        (ring/auth-middleware {:auth-backend auth-backend
                               :get-current-user model/get-current-user}))
      (plugins/plugin-middleware opts)
      [ring/wrap-no-cache
       ring/wrap-tap])))

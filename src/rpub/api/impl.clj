(ns rpub.api.impl
  {:no-doc true}
  (:require [rpub.admin.impl :as admin-impl]))

(defn api-middleware [opts]
  (let [defaults (-> (admin-impl/site-defaults (assoc opts :auth-required true))
                     (assoc-in [:security :frame-options] false))
        opts' (-> opts
                  (dissoc :content-security-policy)
                  (assoc ::admin-impl/defaults defaults))]
    (admin-impl/admin-middleware opts')))

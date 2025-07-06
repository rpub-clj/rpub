(ns rpub.plugins.api
  {:no-doc true}
  (:require [buddy.auth.backends :as buddy-backends]
            [ring.middleware.defaults :as defaults]
            [rpub.lib.plugins :as plugins]
            [rpub.lib.ring :as ring]
            [rpub.model.users :as users]))

(defn- get-secret [opts]
  (ring/get-session-store-key opts))

(defn- auth-middleware [opts]
  (ring/auth-middleware
    {:auth-backend (buddy-backends/jws
                     {:secret (get-secret opts)
                      :authfn #(update % :id parse-uuid)
                      :token-name "Bearer"})
     :get-current-user users/get-current-user}))

(defn api-middleware [opts]
  (let [opts' (merge {:auth-required true} opts)
        {:keys [auth-required]} opts']
    (concat
      [[defaults/wrap-defaults (ring/api-defaults opts')]]
      (when auth-required (auth-middleware opts))
      (plugins/plugin-middleware opts)
      [ring/wrap-no-cache
       ring/wrap-trace])))

#_(defn- token-handler [{:keys [model] :as req}]
    (let [username (get-in req [:body-params :username])
          raw-password (get-in req [:body-params :password])
          [found-user] (model/get-users model {:usernames [username]
                                               :password true})
          authorized (when found-user
                       (model/verify-password found-user raw-password))]
      (if-not authorized
        {:status 401, :headers {}, :body nil}
        (let [current-user (select-keys found-user [:id])
              token (jwt/sign current-user (get-secret req))]
          (response/response {:token token})))))

(defn routes [_]
  [#_["/api/token" {:post #'token-handler}]])

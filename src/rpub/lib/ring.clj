(ns rpub.lib.ring
  (:require [buddy.auth :as buddy-auth]
            [buddy.auth.middleware :as buddy-middleware]
            [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.string :as str]
            [reitit.ring :as reitit-ring]
            [ring.middleware.defaults :as defaults]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.response :as response]
            [rpub.lib.secrets :as secrets]))

(defn wrap-rename-keys [handler kmap]
  (fn [req] (handler (set/rename-keys req kmap))))

(defn skip-auth? [req]
  (#{"/admin/login" "/api/token"}
   (-> req reitit-ring/get-match :path)))

(defn authenticated? [req]
  (boolean (:current-user req)))

(defn wrap-auth-required [handler]
  (fn [req]
    (if (or (skip-auth? req) (authenticated? req))
      (handler req)
      (buddy-auth/throw-unauthorized))))

(defn unauthorized-response []
  (response/status 403))

(defn unauthorized-handler [{:keys [uri] :as req} _metadata]
  (if (authenticated? req)
    (unauthorized-response)
    (-> (response/redirect "/admin/login")
        (assoc-in [:flash :redirect-to] uri))))

(defn wrap-current-user [handler get-current-user]
  (fn [req]
    (let [user (some-> (get-current-user req) (select-keys [:username]))
          req' (update req :current-user merge user)]
      (handler req'))))

(defn script-hash [s]
  (let [hash (-> s codecs/str->bytes hash/sha256 codecs/bytes->b64-str)]
    (format "'sha256-%s'" hash)))

(defn default-content-security-policy [req {:keys [extra-script-src]}]
  (let [inline-script-hashes (map script-hash (:inline-scripts req))
        extra-script-src-strs (when extra-script-src (extra-script-src req))
        script-src-strs (concat inline-script-hashes extra-script-src-strs)]
    (->> ["default-src 'self'"
          (str "script-src 'self' " (str/join " " script-src-strs))
          "style-src 'self' 'unsafe-inline'"
          "img-src 'self' data:"
          "font-src 'self'"
          "connect-src 'self'"
          "object-src 'none'"
          "frame-ancestors 'none'"
          "base-uri 'self'"
          "form-action 'self'"]
         (filter identity)
         (str/join "; "))))

(defn wrap-content-security-policy
  ([handler] (wrap-content-security-policy handler nil))
  ([handler config]
   (fn [req]
     (let [v (default-content-security-policy req config)
           headers {"Content-Security-Policy" v}
           res (handler req)]
       (update res :headers merge headers)))))

(defn wrap-no-cache [handler]
  (fn [req]
    (-> (handler req)
        (update :headers merge
                {"Cache-Control" "no-cache, no-store, must-revalidate"
                 "Pragma" "no-cache"
                 "Expires" "0"}))))

(defn wrap-tap [handler]
  (fn [req]
    (let [res (handler req)]
      (tap> {:req req :res res})
      res)))

(defn- decrypt-session-store-key [encrypted-session-store-key secret-key]
  (-> (secrets/decrypt encrypted-session-store-key secret-key)
      codecs/hex->bytes))

(defn get-session-store-key
  [{:keys [session-store-key config secret-key-file]
    :as _opts}]
  (or session-store-key
      (let [secret (edn/read-string {:readers *data-readers*}
                                    (:encrypted-session-store-key config))]
        (decrypt-session-store-key
          secret
          (secrets/get-secret-key secret-key-file)))))

(defn- session-defaults [opts]
  (let [session-store-key (get-session-store-key opts)
        session-store (cookie/cookie-store {:key session-store-key})
        cookie-max-age (* 60 60 24 7)]
    {:cookie-name "rpub-session"
     :cookie-attrs {:same-site :strict
                    :max-age cookie-max-age}
     :store session-store}))

(defn site-defaults [opts]
  (let [opts' (merge {:auth-required true} opts)
        {:keys [auth-required]} opts']
    (cond-> (-> defaults/site-defaults
                (select-keys [:cookies :responses :security :session])
                (assoc :proxy false)
                (assoc :params {:multipart {:max-file-size (* 5 1024 1024)
                                            :max-file-count 10}})
                (update :responses merge {:content-types false})
                (update :security merge {:content-type-options false})
                (update :session merge (session-defaults opts)))
      (not auth-required) (assoc-in [:security :anti-forgery] false))))

(defn api-defaults [opts]
  (let [opts' (merge {:auth-required true} opts)
        {:keys [auth-required]} opts']
    (cond-> (-> defaults/api-defaults
                (select-keys [:responses :security])
                (assoc :proxy false)
                (assoc :params {:multipart {:max-file-size (* 5 1024 1024)
                                            :max-file-count 10}})
                (update :responses merge {:content-types false})
                (update :security merge {:content-type-options false}))
      (not auth-required) (assoc-in [:security :anti-forgery] false))))

(defn auth-middleware [{:keys [auth-backend get-current-user] :as _opts}]
  [[buddy-middleware/wrap-authentication auth-backend]
   [buddy-middleware/wrap-authorization auth-backend]
   [wrap-rename-keys {:identity :current-user}]
   wrap-auth-required
   [wrap-current-user get-current-user]])

(ns rpub.plugins.app.helpers
  (:require [reitit.core :as reitit]
            [ring.middleware.defaults :as defaults]
            [rpub.lib.plugins :as plugins]
            [rpub.lib.ring :as ring]))

(defn url-for
  ([content-item] (url-for content-item nil))
  ([content-item opts]
   (let [{:keys [permalink-router site-base-url]} opts
         path-params {:content-type-slug (get-in content-item [:content-type :slug])
                      :content-item-slug (get-in content-item [:fields "Slug"])}
         match (reitit/match-by-name permalink-router :single path-params)
         path (reitit/match->path match)]
     (str site-base-url path))))

(defn- site-defaults [_]
  (-> defaults/site-defaults
      (select-keys [:responses :security])
      (assoc :proxy false)
      (update :responses merge {:content-types false})
      (update :security merge {:content-type-options false})))

(defn wrap-cache [handler]
  (fn [req]
    (when-let [res (handler req)]
      (let [headers {"Cache-Control"
                     (str "public, s-maxage=31536000, max-age=0, "
                          "must-revalidate")}]
        (update res :headers merge headers)))))

(defn app-middleware [{:keys [content-security-policy] :as opts}]
  (concat [[defaults/wrap-defaults (site-defaults opts)]]
          (plugins/plugin-middleware opts)
          (when content-security-policy
            [ring/wrap-content-security-policy])
          [wrap-cache
           ring/wrap-trace]))

(ns rpub.app
  {:no-doc true}
  (:require [clojure.edn :as edn]
            [hiccup2.core :as hiccup]
            [markdown.core :as md]
            [reitit.core :as reitit]
            [reitit.ring :as reitit-ring]
            [ring.middleware.defaults :as defaults]
            [rpub.lib.html :as html]
            [rpub.lib.plugins :as plugins]
            [rpub.lib.ring :as ring]
            [rpub.model :as model]
            [rpub.plugins.content-types :as content-types]))

(defn url-for
  ([content-item] (url-for content-item nil))
  ([content-item opts]
   (let [{:keys [permalink-router site-base-url]} opts
         path-params {:content-type-slug (get-in content-item [:content-type :slug])
                      :content-item-slug (get-in content-item [:fields "Slug"])}
         match (reitit/match-by-name permalink-router :single path-params)
         path (reitit/match->path match)]
     (str site-base-url path))))

(defn page-layout [& {:keys [title content head]}]
  (str
    (hiccup/html
      {:mode :html}
      (hiccup/raw "<!DOCTYPE html>")
      [:html {:lang "en"}
       [:head
        [:meta {:charset "utf-8"}]
        [:meta {:name "viewport"
                :content "width=device-width, initial-scale=1.0"}]
        head
        [:title title]
        [:link {:href "/feeds/main"
                :type "application/atom+xml"
                :rel "alternate"}]
        [:link {:rel "icon" :href "/favicon.ico" :type "image/x-icon"}]
        [:link {:rel "shortcut icon" :href "/favicon.ico" :type "image/x-icon"}]]
       [:body
        content]])))

(defn index-handler [{:keys [head settings] :as req}]
  (let [active-theme (model/active-theme req)
        settings' (update-in settings
                             [:footer-links :value]
                             #(some-> % edn/read-string))
        setting-value-map (update-vals settings' :value)
        req' (merge req {:page setting-value-map})]
    {:status 200
     :body (page-layout
             {:title (-> req' :page :site-title)
              :content ((:index-page active-theme) req')
              :head head})}))

(defn content-item-page [{:keys [head path-params settings] :as req}]
  (let [active-theme (model/active-theme req)
        settings' (update-in settings
                             [:footer-links :value]
                             #(some-> % edn/read-string))
        setting-value-map (update-vals settings' :value)
        {:keys [content-type-slug content-item-slug]} path-params
        [content-item] (content-types/get-content-items
                         (::content-types/model req)
                         (cond-> {:content-item-slugs [content-item-slug]}
                           content-type-slug (assoc :content-type-slugs [content-type-slug])))
        req' (merge req {:page (merge {:title (get-in content-item [:fields "Title"])}
                                      setting-value-map)
                         :post content-item})]
    (when content-item
      {:status 200
       :body (page-layout
               {:title (-> req' :page :title)
                :content ((:post-page active-theme) req')
                :head head})})))

(defn atom-feed-response [{:keys [title entries]}]
  {:status 200
   :headers {"Content-Type" "application/atom+xml"}
   :body (str
           (hiccup/html
             {:mode :xhtml}
             (hiccup/raw "<?xml version=\"1.0\" encoding=\"utf-8\"?>")
             [:feed {:xmlns "http://www.w3.org/2005/Atom"}
              [:title title]
              (for [entry entries]
                [:entry
                 [:title (:title entry)]
                 [:link {:href (:href entry)}]
                 [:published (:published entry)]
                 (when (:updated entry) [:updated (:updated entry)])
                 [:content {:type :html}
                  (md/md-to-html-string (:content entry))]
                 [:author [:name (get-in entry [:author :name])]]])]))})

(defn- post->feed-entry [post opts]
  (let [{:keys [id fields created-at updated-at created-name]} post]
    (cond-> {:id (str "urn:uuid:" id)
             :href (url-for post opts)
             :title (get fields "Title")
             :published (str created-at)
             :content (get fields "Content")
             :author {:name created-name}}
      updated-at (assoc :updated updated-at))))

(defn main-feed-handler [{:keys [settings] :as req}]
  (let [{:keys [content-type-slugs]} (:data (reitit-ring/get-match req))
        posts (content-types/get-content-items
                (::content-types/model req)
                {:content-type-slugs content-type-slugs})
        feed-entries (map #(post->feed-entry % req) posts)
        site-title (get-in settings [:site-title :value])]
    (atom-feed-response {:title site-title
                         :entries feed-entries})))

(defn wrap-cache [handler]
  (fn [req]
    (let [res (handler req)
          headers (cond-> {"Cache-Control" (str "public, s-maxage=31536000, max-age=0, "
                                                "must-revalidate")})]
      (update res :headers merge headers))))

(defn- parse-permalink-uri [{:keys [uri permalink-router] :as _req}]
  (:path-params (reitit/match-by-path permalink-router uri)))

(defn- permalink-handler [req]
  (when-let [path-params (parse-permalink-uri req)]
    (let [req' (update req :path-params merge path-params)]
      (content-item-page req'))))

(defn wrap-default-handlers [handler & {:as opts}]
  (reitit-ring/routes
    (reitit-ring/create-resource-handler
      {:parameter :path
       :not-found-handler (constantly nil)})
    handler
    (reitit-ring/redirect-trailing-slash-handler {:method :strip})
    (fn [_] (html/not-found opts))))

(defn- site-defaults [_]
  (-> defaults/site-defaults
      (select-keys [:responses :security])
      (assoc :proxy false)
      (update :responses merge {:content-types false})
      (update :security merge {:content-type-options false})))

(defn app-middleware [{:keys [content-security-policy] :as opts}]
  (concat [[defaults/wrap-defaults (site-defaults opts)]]
          (plugins/plugin-middleware opts)
          (when content-security-policy
            [ring/wrap-content-security-policy])
          [wrap-cache]))

(defn routes [opts]
  [["" {:middleware (app-middleware opts)}
    [["/" {:get #'index-handler}]
     ["/feeds/main" {:get #'main-feed-handler
                     :content-type-slugs [:posts]}]
     ["*path" {:get #'permalink-handler
               :middleware [wrap-default-handlers]}]]]])

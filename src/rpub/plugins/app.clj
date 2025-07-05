(ns rpub.plugins.app
  {:no-doc true}
  (:require [clojure.edn :as edn]
            [hiccup2.core :as hiccup]
            [markdown.core :as md]
            [reitit.core :as reitit]
            [rpub.model :as model]
            [rpub.plugins.app.helpers :as helpers]
            [rpub.plugins.content-types :as content-types]))

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
             :href (helpers/url-for post opts)
             :title (get fields "Title")
             :published (str created-at)
             :content (get fields "Content")
             :author {:name created-name}}
      updated-at (assoc :updated updated-at))))

(defn main-feed-handler [{:keys [settings ::content-type-slugs] :as req}]
  (let [posts (content-types/get-content-items
                (::content-types/model req)
                {:content-type-slugs content-type-slugs})
        feed-entries (map #(post->feed-entry % req) posts)
        site-title (get-in settings [:site-title :value])]
    (atom-feed-response {:title site-title
                         :entries feed-entries})))

(defn- parse-permalink-uri [{:keys [uri permalink-router] :as _req}]
  (:path-params (reitit/match-by-path permalink-router uri)))

(defn- permalink-handler [req]
  (when-let [path-params (parse-permalink-uri req)]
    (let [req' (update req :path-params merge path-params)]
      (content-item-page req'))))

(defn wildcard-handler [{:keys [uri] :as req}]
  (case uri
    "/" (index-handler req)
    "/feeds/main" (main-feed-handler
                    (assoc req ::content-type-slugs [:posts]))
    (permalink-handler req)))

(defn routes [opts]
  [["*path" {:handler wildcard-handler
             :middleware (helpers/app-middleware opts)}]])

(defmethod model/internal-plugin ::plugin [_]
  {:routes routes
   :wildcard true})

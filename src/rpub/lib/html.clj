(ns rpub.lib.html
  {:no-doc true}
  (:require [babashka.fs :as fs]
            [babashka.json :as json]
            [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [cognitect.transit :as transit]
            [hiccup2.core :as hiccup]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.util.response :as response])
  (:import (java.io ByteArrayInputStream ByteArrayOutputStream)
           (java.time Instant)))

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

(def transit-write-handlers
  {Instant (transit/write-handler "time/instant" str)})

(defn read-transit [s]
  (let [in (ByteArrayInputStream. (.getBytes s))]
    (transit/read (transit/reader in :json))))

(defn write-transit [x]
  (let [out (ByteArrayOutputStream.)
        writer (transit/writer out :json {:handlers transit-write-handlers})]
    (transit/write writer x)
    (str out)))

(defn- read-json [path]
  (json/read-str (slurp path) {:key-fn str}))

(defn stylesheet-path [file-path]
  (let [resource-path (str "public/css/" file-path)]
    (if (io/resource resource-path)
      (str "/css/" file-path)
      (let [manifest-path (str (fs/path (fs/parent resource-path) "manifest.json"))
            manifest (some-> (io/resource manifest-path) read-json)]
        (if manifest
          (str "/css/" (fs/path (fs/parent file-path) (get manifest (fs/file-name file-path))))
          (throw (ex-info "Couldn't find stylesheet on classpath"
                          {:file-path file-path
                           :resource-path resource-path})))))))

(defn stylesheet-tag
  "Returns the main stylesheet `<link>` tag if the main CSS file is found.
  Otherwise returns `nil`."
  [file-name]
  (when-let [href (stylesheet-path file-name)]
    [:link {:rel "stylesheet" :href href}]))

(defn not-found [{:keys [redirect-to] :or {redirect-to "/"}}]
  (-> (response/not-found
        (str
          (hiccup/html
            {:mode :html}
            (hiccup/raw "<!DOCTYPE html>")
            [:html {:lang "en"}
             [:head
              [:meta {:charset "UTF-8"}]
              [:meta {:http-equiv "refresh" :content (str "0;url=" redirect-to)}]
              [:title "Redirecting..."]]
             [:body]])))
      (update :headers merge
              {"Cache-Control" "no-cache, no-store, must-revalidate"
               "Pragma" "no-cache"
               "Expires" "0"})))

(defn instants->strs [m]
  (walk/postwalk (fn [x] (if (instance? Instant x) (str x) x)) m))

(defn custom-element [element & {:as opts}]
  (let [[k props] element
        opts' (merge {:format :json} opts)
        encode (case (:format opts')
                 :json #(json/write-str (instants->strs %))
                 :transit write-transit)
        props' (update-vals props encode)]
    [k props']))

(def cljs custom-element)

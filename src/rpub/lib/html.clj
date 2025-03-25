(ns rpub.lib.html
  {:no-doc true}
  (:require [babashka.fs :as fs]
            [babashka.json :as json]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [hiccup2.core :as hiccup]
            [ring.util.response :as response]
            [rpub.lib.transit :as transit])
  (:import (java.time Instant)))

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
                 :transit transit/write)
        props' (update-vals props encode)]
    [k props']))

(def cljs custom-element)

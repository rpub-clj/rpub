(ns rpub.lib.permalinks
  (:require [clojure.string :as str]))

(defn- routes [raw-routes]
  (map (fn [[t d]]
         {:template t
          :name (if (string? d) d (:name d))})
       raw-routes))

(defn router [raw-routes]
  {:routes (routes raw-routes)})

(defn- resolve-path [template data]
  (reduce (fn [p [k v]]
            (-> p
                (str/replace (str "{" (name k) "}") v)
                (str/replace (str k) v)))
          template
          data))

(defn match-by-name [router route-name route-data]
  (let [{:keys [routes]} router
        route (some #(when (= (:name %) route-name) %) routes)]
    (merge route {:path (resolve-path (:template route) route-data)})))

(ns rpub.lib.permalinks
  {:no-doc true}
  (:require [reitit.core :as reitit]))

(defn ->permalink-router [{:keys [single]}]
  (reitit/router [[single :single]]))

(defn default-permalink-router []
  (->permalink-router {:single "/{content-type-slug}/{content-item-slug}"}))

(ns rpub.lib.transit
  {:no-doc true}
  (:refer-clojure :exclude [read])
  (:require [cognitect.transit :as transit])
  (:import (java.io ByteArrayInputStream ByteArrayOutputStream)
           (java.time Instant)))

(def ^:private write-handlers
  {Instant (transit/write-handler "time/instant" str)})

(defn read [s]
  (let [in (ByteArrayInputStream. (.getBytes s))]
    (transit/read (transit/reader in :json))))

(defn write [x]
  (let [out (ByteArrayOutputStream.)
        writer (transit/writer out :json {:handlers write-handlers})]
    (transit/write writer x)
    (str out)))

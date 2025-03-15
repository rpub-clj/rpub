(ns rpub.lib.transit
  (:require ["transit-js" :as t]))

(declare transit->cljs)

(def read-handlers
  {"time/instant" #(js/Date. %)
   "list" #(map transit->cljs %)})

(def array-builder
  {:init (fn [_] [])
   :add (fn [ret v _] (conj ret (transit->cljs v)))
   :finalize (fn [ret] ret)})

(def map-builder
  {:init (fn [_] {})
   :add (fn [ret k v _] (assoc ret (transit->cljs k) (transit->cljs v)))
   :finalize (fn [ret] ret)})

(def reader
  (t/reader "json"
            (clj->js {:handlers read-handlers
                      :mapBuilder map-builder
                      :arrayBuilder array-builder})))

(def DateHandler
  (t/makeWriteHandler
    #js{:tag (fn [_v _h] "time/instant")
        :rep (fn [v _h] (.toISOString v))
        :stringRep (fn [_v _h] nil)}))

(def write-handlers (t/map #js[js/Date DateHandler]))
(def writer (t/writer "json" (clj->js {:handlers write-handlers})))

(declare cljs->transit)

(defn cljs-sequential->transit [xs]
  (into-array (map cljs->transit xs)))

(defn cljs-map->transit [m]
  (t/map (cljs-sequential->transit (mapcat identity m))))

(defn cljs-keyword->transit [x]
  (if-let [key-ns (namespace x)]
    (t/keyword (str key-ns "/" (name x)))
    (t/keyword (name x))))

(defn cljs-uuid->transit [s]
  (t/uuid (str s)))

(defn cljs->transit [x]
  (cond
    (uuid? x) (cljs-uuid->transit x)
    (sequential? x) (cljs-sequential->transit x)
    (and (map? x) (not (record? x))) (cljs-map->transit x)
    (keyword? x) (cljs-keyword->transit x)
    :else x))

(defn transit-keyword->cljs [x]
  (if-let [key-ns (.namespace x)]
    (keyword key-ns (.name x))
    (keyword (.name x))))

(defn transit-map->cljs [x]
  (->> (.entries x)
       (map (fn [entry]
              (let [[k v] entry]
                [(transit->cljs k) (transit->cljs v)])))
       (into {})))

(defn transit->cljs [x]
  (cond
    (t/isKeyword x) (transit-keyword->cljs x)
    (t/isUUID x) (uuid (str x))
    :else x))

(defn write [cljs-value]
  (.write writer (cljs->transit cljs-value)))

(defn read [s]
  (transit->cljs (.read reader s)))

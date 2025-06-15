(ns rpub.lib.transit
  {:no-doc true}
  (:require ["transit-js" :as t]))

(declare ^:private transit->cljs)

(def ^:private read-handlers
  {"rpub/instant" #(js/Date. %)
   "list" #(map transit->cljs %)})

(def ^:private array-builder
  {:init (fn [_] [])
   :add (fn [ret v _] (conj ret (transit->cljs v)))
   :finalize (fn [ret] ret)})

(def ^:private map-builder
  {:init (fn [_] {})
   :add (fn [ret k v _] (assoc ret (transit->cljs k) (transit->cljs v)))
   :finalize (fn [ret] ret)})

(def ^:private reader
  (t/reader "json"
            (clj->js {:handlers read-handlers
                      :mapBuilder map-builder
                      :arrayBuilder array-builder})))

(def ^:private DateHandler
  (t/makeWriteHandler
    #js{:tag (fn [_v _h] "rpub/instant")
        :rep (fn [v _h] (.toISOString v))
        :stringRep (fn [_v _h] nil)}))

(def ^:private write-handlers (t/map #js[js/Date DateHandler]))
(def ^:private writer (t/writer "json" (clj->js {:handlers write-handlers})))

(declare ^:private cljs->transit)

(defn- cljs-sequential->transit [xs]
  (into-array (map cljs->transit xs)))

(defn- cljs-map->transit [m]
  (t/map (cljs-sequential->transit (mapcat identity m))))

(defn- cljs-keyword->transit [x]
  (if-let [key-ns (namespace x)]
    (t/keyword (str key-ns "/" (name x)))
    (t/keyword (name x))))

(defn- cljs-uuid->transit [s]
  (t/uuid (str s)))

(defn- cljs->transit [x]
  (cond
    (uuid? x) (cljs-uuid->transit x)
    (sequential? x) (cljs-sequential->transit x)
    (and (map? x) (not (record? x))) (cljs-map->transit x)
    (keyword? x) (cljs-keyword->transit x)
    :else x))

(defn- transit-keyword->cljs [x]
  (if-let [key-ns (.namespace x)]
    (keyword key-ns (.name x))
    (keyword (.name x))))

(defn- transit-set->cljs [x]
  (let [v (transient #{})]
    (.forEach x #(conj! v (transit->cljs %)))
    (persistent! v)))

(defn- transit-uuid->cljs [x]
  (uuid (str x)))

(defn- transit->cljs [x]
  (cond
    (t/isKeyword x) (transit-keyword->cljs x)
    (t/isUUID x) (transit-uuid->cljs x)
    (t/isSet x) (transit-set->cljs x)
    :else x))

(defn write [cljs-value]
  (.write writer (cljs->transit cljs-value)))

(defn read [s]
  (transit->cljs (.read reader s)))

(ns rpub.lib.edn
  (:refer-clojure :exclude [read-string])
  (:require [clojure.edn :as edn]
            [clojure.pprint :as pprint])
  (:import (java.time Instant)))

(defn ->instant [s]
  (Instant/parse s))

(defmethod print-method Instant [record writer]
  (.write writer (str "#inst \"" record "\"")))

(defmethod pprint/simple-dispatch Instant [secret]
  (pr secret))

(def read-opts {:readers (assoc *data-readers* 'inst ->instant)})

(defn read-string [s]
  (edn/read-string read-opts s))

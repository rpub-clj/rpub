(ns rpub.dev.repl
  (:require [clojure.walk :as walk]))

(defonce taps (atom []))

(def max-taps 100)

(defn tap-fn [x]
  (swap! taps (fn [v]
                (if (< (count v) max-taps)
                  (conj v x)
                  (conj (subvec v 1) x)))))

(add-tap #'tap-fn)

(defn req []
  (->> @taps (keep :req) last))

(defn model []
  (let [r (req)]
    (walk/postwalk
      (fn [x] (if (:ds x) (assoc x :ds (:conn r)) x))
      (:model r))))

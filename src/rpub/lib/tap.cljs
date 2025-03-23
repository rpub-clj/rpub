(ns rpub.lib.tap
  {:no-doc true}
  (:require [rpub.lib.http :as http]))

(defn- reify-metadata [x]
  (let [x' (cond
             (and (map? x) (not (record? x))) (-> x
                                                  (update-keys reify-metadata)
                                                  (update-vals reify-metadata))
             (set? x) (into #{} (map reify-metadata) x)
             (list? x) (into () (map reify-metadata) x)
             (vector? x) (into [] (map reify-metadata) x)
             (sequential? x) (sequence (map reify-metadata) x)
             :else x)]
    (if-let [m (meta x)]
      {::value x', ::meta m}
      x')))

(defn remote-tap [url value]
  (let [value' (reify-metadata value)]
    (http/post url {:body value', :format :transit})))

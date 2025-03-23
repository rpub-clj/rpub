(ns rpub.lib.tap
  {:no-doc true}
  (:require [clojure.walk :as walk]
            [ring.util.response :as response]))

(defn handler [req]
  (let [value (get-in req [:body-params])
        value' (walk/postwalk
                 (fn [x]
                   (if-not (::value x)
                     x
                     (with-meta (::value x) (::meta x))))
                 value)]
    (tap> value')
    (-> (response/status 200)
        (assoc :body {:success true}))))

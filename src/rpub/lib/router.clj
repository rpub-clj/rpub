(ns rpub.lib.router
  (:require [clojure.core.cache :as cache]
            [clojure.core.cache.wrapped :as cache-wrapped]
            [reitit.ring :as rr]
            [rpub.lib.otel :as otel]))

(defn routes [{:keys [plugins otel] :as config} & {:keys [wildcard]}]
  ["" {:middleware (if otel
                     [otel/wrap-reitit-route]
                     [])}
   (->> plugins
        (filter #(and (= (boolean (:wildcard %)) (boolean wildcard))
                      (:routes %)))
        (mapcat #((:routes %) config)))])

(defn ->handler-cache []
  (cache/lru-cache-factory {} :threshold 100))

(defonce global-handler-cache
  (atom (->handler-cache)))

(defn- ->ring-handler [{:keys [not-found] :as config}]
  (rr/ring-handler
    (rr/router (routes config :wildcard false))
    (rr/ring-handler
      (rr/router (routes config :wildcard true))
      (rr/routes
        (rr/create-resource-handler {:path "/"})
        (rr/redirect-trailing-slash-handler {:method :strip})
        (rr/create-default-handler {:not-found not-found
                                    :not-acceptable not-found})))))

(defn ring-handler [{:keys [muuntaja route-middleware handler-middleware] :as config}]
  (rr/ring-handler
    (rr/router
      [["*" (fn [{:keys [plugins] :as req}]
              (let [activated (filter :activated plugins)
                    handler (cache-wrapped/lookup-or-miss
                              global-handler-cache
                              (set (map :key activated))
                              (fn [_]
                                (->ring-handler
                                  (-> config
                                      (dissoc :muuntaja :middleware)
                                      (assoc :plugins activated)))))]
                (handler req)))]]
      {:data {:middleware route-middleware
              :muuntaja muuntaja}})
    nil
    {:middleware handler-middleware}))

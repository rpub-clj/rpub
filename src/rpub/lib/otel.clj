(ns rpub.lib.otel)

(defn- add-exception! [& {:as opts}]
  ((requiring-resolve 'steffan-westcott.clj-otel.api.trace.span/add-exception!)
   opts))

(defn wrap-exception-event [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable e
        (add-exception! e {:escaping? false})
        (throw e)))))

(defn wrap-server-span [& {:as opts}]
  ((requiring-resolve 'steffan-westcott.clj-otel.api.trace.span/wrap-server-span)
   opts))

(defn wrap-reitit-route [& {:as opts}]
  ((requiring-resolve 'steffan-westcott.clj-otel.api.trace.http/wrap-reitit-route)
   opts))

(defn dyn []
  ((requiring-resolve 'steffan-westcott.clj-otel.context/dyn)))

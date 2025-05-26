(ns rpub.lib.otel)

(defn- add-exception! [& args]
  (apply (requiring-resolve 'steffan-westcott.clj-otel.api.trace.span/add-exception!)
         args))

(defn wrap-exception-event [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable e
        (add-exception! e {:escaping? false})
        (throw e)))))

(defn wrap-server-span [& args]
  (apply (requiring-resolve 'steffan-westcott.clj-otel.api.trace.http/wrap-server-span)
         args))

(defn wrap-reitit-route [& args]
  (apply (requiring-resolve 'steffan-westcott.clj-otel.api.trace.http/wrap-reitit-route)
         args))

(defn dyn [& args]
  (apply (requiring-resolve 'steffan-westcott.clj-otel.context/dyn)
         args))

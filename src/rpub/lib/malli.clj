(ns rpub.lib.malli)

(defn- pretty-reporter []
  ((requiring-resolve 'malli.dev.pretty/reporter)))

(defn start-dev! [& {:as opts}]
  ((requiring-resolve 'malli.dev/start!)
   (cond-> opts
     (not (:reporter opts)) (assoc :reporter (pretty-reporter)))))

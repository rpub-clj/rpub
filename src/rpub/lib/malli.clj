(ns rpub.lib.malli
  {:no-doc true})

(defn- pretty-reporter []
  ((requiring-resolve 'malli.dev.pretty/reporter)))

(defn start-dev! [& {:as opts}]
  ((requiring-resolve 'malli.dev/start!)
   (cond-> opts
     (not (:report opts)) (assoc :report (pretty-reporter)))))

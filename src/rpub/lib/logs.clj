(ns rpub.lib.logs
  (:require [taoensso.telemere :as tel]))

(defn- otel-xfn [m]
  (if (= (:kind m) :event)
    (merge m {:otel/attrs {:event.type type}
              :msg (str "EVENT " (pr-str type) " " (pr-str (:data m)))})
    m))

(defn- otel-context-dyn []
  ((requiring-resolve 'steffan-westcott.clj-otel.context)))

(defn- base-otel-handler [& {:as opts}]
  ((requiring-resolve 'taoensso.telemere.open-telemetry) opts))

(defn- handler:open-telemetry []
  (let [handler (base-otel-handler {:logger-provider :default})]
    (fn a-handler:open-telemetry
      ([] (handler))
      ([signal] (handler (otel-xfn (assoc signal :otel/context (otel-context-dyn))))))))

(defn setup! [{:keys [otel]}]
  (when otel
    (run! tel/remove-handler! (keys (tel/get-handlers)))
    (tel/add-handler! ::otel (handler:open-telemetry))))

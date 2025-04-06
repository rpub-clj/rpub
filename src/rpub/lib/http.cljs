(ns rpub.lib.http
  {:no-doc true}
  (:require [rpub.lib.transit :as transit]))

(defn- get-csrf-token []
  (-> (js/document.querySelector "meta[name='csrf-token']")
      (.getAttribute "content")))

(defn- post-callback [url {:keys [body on-complete] :as opts}]
  (let [opts' (merge {:format :json} opts)
        mime-type (case (:format opts')
                    :json "application/json"
                    :transit "application/transit+json")
        headers {"X-CSRF-Token" (get-csrf-token)
                 "Accept" mime-type
                 "Content-Type" mime-type}
        read-value (fn [x]
                     (case (:format opts')
                       :json (js->clj (js/JSON.parse x))
                       :transit (transit/read x)))
        write-value (fn [x]
                      (case (:format opts')
                        :json (js/JSON.stringify (clj->js x))
                        :transit (transit/write x)))
        params (cond-> {:method :post, :headers headers}
                 body (assoc :body (write-value body)))
        req (js/fetch url (clj->js params))]
    (when on-complete
      (-> req
          (.then #(.text %))
          (.then #(on-complete (read-value %)) nil)
          (.catch #(on-complete nil %))))
    nil))

(defn- post-promise [url opts]
  (js/Promise.
    (fn [resolve reject]
      (let [on-complete (fn [res err] (if err (reject err) (resolve res)))
            opts' (merge opts {:on-complete on-complete, :format :transit})]
        (post-callback url opts')))))

(defn post [url opts]
  (if (:on-complete opts) ; deprecated option
    (post-callback url opts)
    (post-promise url opts)))

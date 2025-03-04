(ns rpub.lib.http
  {:no-doc true})

(defn post [url {:keys [anti-forgery-token body on-complete]}]
  (let [headers {"X-CSRF-Token" anti-forgery-token
                 "Accept" "application/json"
                 "Content-Type" "application/json"}
        params (cond-> {:method :post, :headers headers}
                 body (assoc :body (js/JSON.stringify (clj->js body))))
        req (js/fetch url (clj->js params))]
    (when on-complete
      (-> req
          (.then #(.text %))
          (.then #(on-complete (js->clj (js/JSON.parse %)) nil))
          (.catch #(on-complete nil %))))
    nil))

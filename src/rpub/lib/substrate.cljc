(ns rpub.lib.substrate)

(defonce global (atom nil))

(defn set-global! [substrate]
  (reset! global substrate))

(defn- assert-valid-selector [selector]
  (when-not (and (vector? selector) (qualified-keyword? (first selector)))
    (throw (ex-info "Invalid selector" {:selector selector}))))

(defn subscribe
  ([selector] (subscribe @global selector))
  ([substrate selector]
   (assert-valid-selector selector)
   ((:subscribe substrate) substrate selector)))

(defn- assert-valid-event [event]
  (when-not (and (vector? event) (qualified-keyword? (first event)))
    (throw (ex-info "Invalid event" {:event event}))))

(defn dispatch
  ([event] (dispatch @global event))
  ([substrate event]
   (assert-valid-event event)
   ((:dispatch substrate) substrate event)))

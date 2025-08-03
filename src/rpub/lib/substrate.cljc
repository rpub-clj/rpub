(ns rpub.lib.substrate)

(defonce global (atom nil))

(defn set-global! [v]
  (reset! global v))

(defn subscribe
  ([selector] (subscribe @global selector))
  ([substrate selector]
   ((:subscribe substrate) substrate selector)))

(defn dispatch
  ([event] (dispatch @global event))
  ([substrate event]
   ((:dispatch substrate) substrate event)))

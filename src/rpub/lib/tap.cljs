(ns rpub.lib.tap
  (:refer-cljure :exclude [add-tap remove-tap tap> *exec-tap-fn*])
  (:require [rpub.lib.http :as http]))

(defn ^{:doc "Arranges to have tap functions executed via the supplied f, a
  function of no arguments. Returns true if successful, false otherwise." :dynamic true}
  *exec-tap-fn*
  [f]
  (and
    (exists? js/setTimeout)
    ;; See CLJS-3274 - workaround for recent WebKit releases
    (boolean (js/setTimeout f 0))))

(defonce ^{:private true} tapset nil)

(defn- maybe-init-tapset []
  (when (nil? tapset)
    (set! tapset (atom #{}))))

(defn add-tap
  "Adds f, a fn of one argument, to the tap set. This function will be called with
  anything sent via tap>. Remember f in order to remove-tap"
  [f]
  (maybe-init-tapset)
  (swap! tapset conj f)
  nil)

(defn remove-tap
  "Remove f from the tap set."
  [f]
  (maybe-init-tapset)
  (swap! tapset disj f)
  nil)

(defn ^boolean tap>
  "Sends x to any taps. Returns the result of *exec-tap-fn*, a Boolean value."
  [x]
  (maybe-init-tapset)
  (*exec-tap-fn*
    (fn []
      (doseq [tap @tapset]
        (try
          (tap x)
          (catch js/Error ex))))))

(defn- reify-metadata [x]
  (let [x' (cond
             (and (map? x) (not (record? x))) (-> x
                                                  (update-keys reify-metadata)
                                                  (update-vals reify-metadata))
             (set? x) (into #{} (map reify-metadata) x)
             (list? x) (into () (map reify-metadata) x)
             (vector? x) (into [] (map reify-metadata) x)
             (sequential? x) (sequence (map reify-metadata) x)
             :else x)]
    (if-let [m (meta x)]
      #:rpub.admin.tap{:value x', :meta m}
      x')))

(defn remote-tap [value]
  (let [value' (reify-metadata value)]
    (http/post "/admin/tap" {:body value', :format :transit})))

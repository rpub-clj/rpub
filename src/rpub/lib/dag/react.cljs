(ns rpub.lib.dag.react
  {:no-doc true}
  (:require ["react"
             :as react
             :refer [useCallback useContext useSyncExternalStore useId]]
            [rpub.lib.dag :as dag]))

(def ^:private DAGContext (react/createContext))

(defn DAGProvider [props]
  (react/createElement
    (.-Provider DAGContext)
    #js{:value {:dag-atom (aget props "dag-atom")}}
    (.-children props)))

(defn- updated? [old-val new-val node-keys]
  (some (fn [k]
          (not= (get-in old-val [::dag/values k])
                (get-in new-val [::dag/values k])))
        node-keys))

(defn- subscribe [dag-atom component-id node-keys on-change]
  (swap! dag-atom
         (fn [current-dag]
           (reduce (fn [d k]
                     (if-not (vector? k)
                       d
                       (let [[parent opts] k
                             calc-fn (fn [db]
                                       (let [f (get-in d [::dag/nodes parent :calc])]
                                         (f db opts)))]
                         (dag/add-node d k {:calc calc-fn} [[parent k]]))))
                   current-dag
                   node-keys)))
  (add-watch dag-atom component-id
             (fn [_ _ old-val new-val]
               (when (updated? old-val new-val node-keys)
                 (on-change))))
  (fn unsubscribe []
    (remove-watch dag-atom component-id)
    (swap! dag-atom
           (fn [current-dag]
             (reduce (fn [d k]
                       (if-not (vector? k)
                         d
                         (dag/remove-node d k)))
                     current-dag
                     node-keys)))))

(defn use-dag
  ([] (use-dag nil))
  ([node-keys]
   (let [{:keys [dag-atom]} (useContext DAGContext)
         component-id (useId)
         sub (useCallback #(subscribe dag-atom component-id node-keys %) #js[])
         get-snapshot (useCallback #(deref dag-atom) #js[])
         dag (useSyncExternalStore sub get-snapshot)
         #_#__ (when (dag/assertions-enabled?)
                 (dag/assert-valid-node-keys dag node-keys))
         values (-> (::dag/values dag) (select-keys node-keys))
         push (useCallback
                (fn [& args]
                  (let [d' (swap! dag-atom
                                  (fn [d]
                                    (when (dag/assertions-enabled?)
                                      (dag/assert-valid-node-keys d (take 1 args)))
                                    (apply dag/push d args)))]
                    (-> (::dag/values d') (select-keys node-keys))))
                #js[])]
     [values push])))

(defn use-dag-values [node-keys]
  (let [{:keys [dag-atom]} (useContext DAGContext)]
    (useCallback
      (fn []
        (-> (::dag/values @dag-atom) (select-keys node-keys))))))

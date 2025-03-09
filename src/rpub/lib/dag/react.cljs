(ns rpub.lib.dag.react
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
  (some #(not= (get-in old-val [::dag/values %])
               (get-in new-val [::dag/values %]))
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
                         (dag/add-node d
                                       k
                                       {:calc calc-fn}
                                       [[parent k]]))))
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

(defn use-dag [node-keys]
  (let [{:keys [dag-atom]} (useContext DAGContext)
        component-id (useId)
        sub #(subscribe dag-atom component-id node-keys %)
        get-snapshot #(deref dag-atom)
        dag (useSyncExternalStore sub get-snapshot)
        values (-> (::dag/values dag) (select-keys node-keys))
        push (useCallback
               (fn
                 ([k] (swap! dag-atom dag/push k))
                 ([k v] (swap! dag-atom dag/push k v)))
               #js[])]
    [values push]))

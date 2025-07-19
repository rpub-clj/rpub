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
          (if (vector? k)
            (let [[parent opts] k
                  old-v ((get-in old-val [::dag/nodes parent :calc]) (dag/calc-input old-val) opts)
                  new-v ((get-in new-val [::dag/nodes parent :calc]) (dag/calc-input new-val) opts)]
              (not= old-v new-v))
            (not= (get-in old-val [::dag/values k])
                  (get-in new-val [::dag/values k]))))
        node-keys))

(defn- subscribe [dag-atom component-id node-keys on-change]
  (add-watch dag-atom component-id
             (fn [_ _ old-val new-val]
               (when (updated? old-val new-val node-keys)
                 (on-change))))
  (fn unsubscribe []
    (remove-watch dag-atom component-id)))

(defn use-dag
  ([] (use-dag nil))
  ([node-keys]
   (let [{:keys [dag-atom]} (useContext DAGContext)
         component-id (useId)
         sub (useCallback #(subscribe dag-atom component-id node-keys %) #js[])
         get-snapshot (useCallback #(deref dag-atom) #js[])
         dag (useSyncExternalStore sub get-snapshot)
         _ (when (dag/assertions-enabled?)
             (dag/assert-valid-node-keys
               dag
               (map #(if (vector? %) (first %) %) node-keys)))
         values (reduce (fn [acc k]
                          (if (vector? k)
                            (let [[parent opts] k
                                  f (get-in dag [::dag/nodes parent :calc])]
                              (assoc acc k (f (dag/calc-input dag) opts)))
                            (assoc acc k (get (::dag/values dag) k))))
                        {}
                        node-keys)
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

(defn use-sub [k]
  (let [[v] (use-dag [k])]
    (get v k)))

(defn use-dispatch []
  (let [[_ push] (use-dag nil)]
    (useCallback #(apply push %) #js[])))

(deftype Delay [f ref]
  IDeref
  (-deref [_]
    (if (= (count ref) 1)
      (first ref)
      (let [v (f)]
        (aset ref 0 v)
        v))))

(defn delay [f]
  (->Delay f #js[]))

(defn use-sub-deref [k]
  (let [{:keys [dag-atom]} (useContext DAGContext)]
    (delay
      (fn []
        (let [dag @dag-atom]
          (if (vector? k)
            (let [[parent opts] k
                  f (get-in dag [::dag/nodes parent :calc])]
              (f (dag/calc-input dag) opts))
            (get (::dag/values dag) k)))))))

(defn use-dag-values [node-keys]
  (let [{:keys [dag-atom]} (useContext DAGContext)]
    (useCallback
      (fn []
        (-> (::dag/values @dag-atom) (select-keys node-keys))))))

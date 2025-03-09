(ns rpub.lib.dag
  (:require [rads.dependency :as dep]))

(defn- dependents [{:keys [::nodes ::edges]}]
  (let [sorted (dep/topo-sort edges)]
    (->> (dep/nodes edges)
         (filter #(get-in nodes [% :push]))
         (map (fn [node-key]
                [node-key (filter (dep/transitive-dependents edges node-key)
                                  sorted)]))
         (into {}))))

(defn- add-edges [graph edges]
  (reduce (fn [g [from to]] (dep/depend g to from))
          graph
          edges))

(defn ->dag [{:keys [nodes edges]}]
  (let [edges' (-> (dep/graph) (add-edges edges))]
    {::nodes nodes
     ::edges edges'
     ::values {}
     ::dependents (dependents {::nodes nodes, ::edges edges'})}))

(defn- recalculate [dag node-key]
  (let [calc-fn (get-in dag [::nodes node-key :calc])
        calc-input (assoc (:acc dag) ::values (::values dag))
        new-val (calc-fn calc-input)]
    (assoc-in dag [::values node-key] new-val)))

(defn push
  ([dag node-key] (push dag node-key ::no-value))
  ([dag node-key v]
   (let [push-fn (get-in dag [::nodes node-key :push])
         dependents (get-in dag [::dependents node-key])
         dag' (if (= v ::no-value)
                (update dag :acc push-fn)
                (update dag :acc push-fn v))]
     (reduce recalculate dag' dependents))))

(defn add-node [dag node-key node-config edges]
  (let [edges' (add-edges (::edges dag) edges)
        dag' (-> dag
                 (assoc-in [::nodes node-key] node-config)
                 (assoc ::edges edges'))]
    (-> dag'
        (assoc ::dependents (dependents dag'))
        (recalculate node-key))))

(defn remove-node [dag node-key]
  (let [dag' (-> dag
                 (update ::nodes dissoc node-key)
                 (update ::edges dep/remove-all node-key))]
    (assoc dag' ::dependents (dependents dag'))))

(defn- trace [f]
  (when f
   (fn [& args]
     (let [ret (apply f args)]
       (prn f {:args args, :ret ret})
       ret))))

(defn add-tracing [dag-config]
  (update dag-config ::nodes
          (fn [nodes]
            (update-vals nodes (comp
                                 #(update % :push trace)
                                 #(update % :calc trace))))))

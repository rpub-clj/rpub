(ns rpub.lib.dag
  {:no-doc true}
  (:require [rads.dependency :as dep]))

(def ^:private assertions-enabled (atom false))

(defn assertions-enabled? []
  @assertions-enabled)

(defn enable-assertions! []
  (reset! assertions-enabled true))

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

(defn- valid-edge-config? [{:keys [nodes] :as _dag-config} edge]
  (every? #(contains? nodes %) edge))

(defn- assert-valid-edges-config [{:keys [edges] :as dag-config}]
  (let [invalid-edges (remove #(valid-edge-config? dag-config %) edges)]
    (when (seq invalid-edges)
      (throw (ex-info (str "Invalid edges config: " (pr-str invalid-edges))
                      {:invalid-edges invalid-edges})))))

(defn ->dag [{:keys [nodes edges] :as dag-config}]
  (when (assertions-enabled?)
    (assert-valid-edges-config dag-config))
  (let [edges' (-> (dep/graph) (add-edges edges))]
    {::nodes nodes
     ::edges edges'
     ::values {}
     ::dependents (dependents {::nodes nodes, ::edges edges'})}))

(defn calc-input [dag]
  (assoc (::acc dag) ::values (::values dag)))

(defn- recalculate [dag node-key]
  (let [calc-fn (get-in dag [::nodes node-key :calc])
        new-val (calc-fn (calc-input dag))]
    (assoc-in dag [::values node-key] new-val)))

(defn- assert-contains-node [dag node-key]
  (when-not (contains? (::nodes dag) node-key)
    (throw (ex-info (str "Unknown node: " node-key) {:node-key node-key}))))

(defn push
  ([dag node-key] (push dag node-key ::no-value))
  ([dag node-key v]
   (when (assertions-enabled?)
     (assert-contains-node dag node-key))
   (let [push-fn (get-in dag [::nodes node-key :push])
         dependents (get-in dag [::dependents node-key])
         dag' (if (= v ::no-value)
                (update dag ::acc push-fn)
                (update dag ::acc push-fn v))]
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

(defn wrap-tracing [dag xf]
  (let [wrap-fn (fn [k f]
                  (fn [& args]
                    (let [ret (apply f args)
                          node {:key k, :args args, :ret ret}]
                      (transduce xf (constantly nil) [node])
                      ret)))
        wrap-node (fn [k v]
                    (cond-> v
                      (:calc v) (update :calc #(wrap-fn k %))
                      (:push v) (update :push #(wrap-fn k %))))]
    (update dag ::nodes
            (fn [nodes]
              (->> nodes
                   (map (fn [[k v]] [k (wrap-node k v)]))
                   (into {}))))))

(defn merge-configs [& configs]
  (reduce #(merge-with into %1 %2) {:nodes {}, :edges []} configs))

(defn nodes [dag]
  (::nodes dag))

(defn edges [dag]
  (mapcat (fn [[from deps]]
            (map (fn [to] [from to])
                 deps))
          (-> dag ::edges :dependencies)))

(defn assert-valid-node-keys [dag node-keys]
  (when-not (every? (::nodes dag) node-keys)
    (throw (ex-info (str "Invalid node keys: " (pr-str node-keys))
                    {:node-keys node-keys}))))

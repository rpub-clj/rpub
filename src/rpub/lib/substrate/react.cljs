(ns rpub.lib.substrate.react
  (:require ["react" :refer [useSyncExternalStore useId]]))

(defn subscribe [{:keys [conn query]} selector]
  (let [component-id (useId)
        sub (fn [callback]
              (add-watch conn component-id (fn [& _] (callback)))
              #(remove-watch conn component-id))
        get-snapshot #(query @conn selector)]
    (useSyncExternalStore sub get-snapshot)))

(defn dispatch [{:keys [conn transact]} event]
  (swap! conn transact event))

(defn substrate [{:keys [conn query transact]}]
  {:conn conn
   :query query
   :transact transact
   :subscribe subscribe
   :dispatch dispatch})

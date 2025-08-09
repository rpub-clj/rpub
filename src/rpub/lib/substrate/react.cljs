(ns rpub.lib.substrate.react
  (:require ["react" :refer [useSyncExternalStore useId]]))

(defn query [db model selector]
  (let [f (get-in model [:queries (first selector)])]
    (f db selector)))

(defn transact [db model event]
  (let [f (get-in model [:transactions (first event)])]
    (f db event)))

(defn subscribe [{:keys [conn model]} selector]
  (let [component-id (useId)
        sub (fn [callback]
              (add-watch conn component-id (fn [& _] (callback)))
              #(remove-watch conn component-id))
        get-snapshot (fn [_] @conn)
        db (useSyncExternalStore sub get-snapshot)]
    (query db model selector)))

(defn dispatch [{:keys [conn model]} event]
  (swap! conn transact model event))

(defn substrate [{:keys [conn model]}]
  {:conn conn
   :model model
   :subscribe subscribe
   :dispatch dispatch})

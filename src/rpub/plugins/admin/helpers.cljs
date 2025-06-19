(ns rpub.plugins.admin.helpers
  {:no-doc true}
  (:require ["flowbite" :as flowbite]
            ["react" :refer [useEffect useState]]
            [rpub.lib.dag :as dag]
            [rpub.lib.dag.react :refer [DAGProvider]]
            [rpub.lib.html :as html]
            [rpub.lib.reagent :as r]))

(defonce dag-atom (atom nil))

(defn node->trace [node]
  (let [pprint-meta {:portal.viewer/default :portal.viewer/pprint}
        add-meta (fn [x] (if (coll? x) (with-meta x pprint-meta) x))
        node' (-> node
                  (dissoc :key)
                  (update :args #(map add-meta %))
                  (update :ret add-meta))]
    [(:key node) node']))

(def tracing-xf
  (comp
    (map node->trace)
    (map tap>)))

(defn wrap-component [f {:keys [page-id prepend-element dag-config tracing]}]
  (fn [props]
    (let [[v set-v] (useState false)]
      (useEffect (fn []
                   (let [dag (cond-> (dag/->dag dag-config)
                               tracing (dag/wrap-tracing tracing-xf))]
                     (set-v (reset! dag-atom dag))))
                 #js[])
      (useEffect (fn [] (when v (flowbite/initFlowbite))) #js[v])
      #jsx [DAGProvider {:dag-atom dag-atom}
            (when v
              (r/as-element
                (list
                  (when prepend-element
                    (prepend-element {:page-id page-id, :dag v}))
                  [f props])))])))

(defn add-page
  [{:keys [page-id component dag-config prepend-element tracing]
    :as _page-config}]
  (let [component' (wrap-component component
                                   {:page-id page-id
                                    :dag-config dag-config
                                    :prepend-element prepend-element
                                    :tracing tracing})]
    (html/add-element page-id component' {:format :transit})))

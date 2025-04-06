(ns rpub.dev.dag.viz
  {:no-doc true}
  (:require ["@xyflow/react" :refer [Background Controls MiniMap ReactFlow
                                     addEdge useNodesState useEdgesState
                                     Handle Position]]
            ["react" :refer [useId useEffect useState useCallback]]
            [clojure.string :as str]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.dag :as dag-lib]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]))

(defn- custom-node [^:js {:keys [data]}]
  #jsx[:div.react-flow__node-default
       {:style "width: 400px; border: 1px solid #ccc; padding: 1rem; border-radius: 6px; background: #fff"}
       [Handle {:type "source" :position (.-Left Position)}]
       (.-label data)
       [Handle {:type "target" :position (.-Right Position)}]])

(defn initial-nodes [dag]
  (map-indexed (fn [i [k _]]
                 {:id (pr-str k)
                  :type "custom-node"
                  :position {:x 0, :y (* i 100)}
                  :data {:label (str k)}})
               (dag-lib/nodes dag)))

(defn initial-edges [dag]
  (map (fn [[from to :as edge]]
         {:id (str edge)
          :source (pr-str from)
          :target (pr-str to)})
       (dag-lib/edges dag)))

(def node-types {:custom-node custom-node})
(def edge-types {})

(def save-debounce-ms 100)

(defn get-dag-metadata [storage-id on-complete]
  (http/post "/admin/api/get-dag-metadata"
             {:format :transit
              :body {:storage-id storage-id}
              :on-complete on-complete}))

(def update-dag-metadata!
  (html/debounce
    (fn [storage-id {:keys [nodes]}]
      (let [saved-nodes (map #(select-keys % [:id :position])
                             (js->clj nodes :keywordize-keys true))
            unsaved-changes (admin-impl/->unsaved-changes
                              storage-id
                              {:saved-nodes saved-nodes})
            http-opts {:format :transit
                       :body unsaved-changes
                       :on-complete (fn [_ err]
                                      (when err
                                        (js/console.log err)))}]
        (http/post "/admin/api/update-dag-metadata" http-opts)))
    save-debounce-ms))

(defn- index-by [f coll]
  (->> coll
       (map (fn [v] [(f v) v]))
       (into {})))

(defn- view-node [node aliases]
  (let [label (-> node .-data .-label)
        label' (reduce (fn [l [from to]]
                         (if (str/starts-with? l from)
                           (reduced (str to (subs l (count from))))
                           l))
                       label
                       aliases)
        data (js/Object.assign #js{} (.-data node) #js{:label label'})]
    (js/Object.assign #js{} node #js{:data data})))

(defn overlay*
  [{:keys [aliases storage-id initial-nodes dag-metadata initial-edges visible]}]
  (let [saved-nodes-index (index-by :id (:saved-nodes dag-metadata))
        initial-nodes' (map #(merge % (get saved-nodes-index (:id %)))
                            initial-nodes)
        [save-skipped set-save-skipped] (useState false)
        [nodes _ on-nodes-change] (useNodesState (clj->js initial-nodes'))
        [edges set-edges on-edges-change] (useEdgesState (clj->js initial-edges))
        on-connect (useCallback
                     (fn [connection] (set-edges #(addEdge connection %)))
                     #js[set-edges])
        on-nodes-change' (fn [changes]
                           (let [ret (on-nodes-change changes)]
                             (if save-skipped
                               (update-dag-metadata! storage-id {:nodes nodes, :edges edges})
                               (set-save-skipped true))
                             ret))
        on-edges-change' (fn [changes]
                           (let [ret (on-edges-change changes)]
                             (if save-skipped
                               (update-dag-metadata! storage-id {:nodes nodes, :edges edges})
                               (set-save-skipped true))
                             ret))
        fit-view true]
    [:div.absolute.inset-0.bg-white.z-50
     {:class (when-not visible "hidden")}
     #jsx[ReactFlow {:nodes (.map nodes #(view-node % aliases))
                     :nodeTypes (clj->js node-types)
                     :onNodesChange on-nodes-change'
                     :edges edges
                     :edgeTypes (clj->js edge-types)
                     :onEdgesChange on-edges-change'
                     :onConnect on-connect
                     :fitView fit-view}
          [MiniMap]
          [Controls]]]))

(defn overlay [{:keys [storage-id] :as props}]
  (let [[first-render set-first-render] (useState false)
        [visible set-visible] (useState false)
        [dag-metadata set-dag-metadata] (useState nil)]
    (useEffect
      (fn []
        (let [hash-handler (fn []
                             (set-first-render true)
                             (set-visible not))
              key-match? (fn [event]
                           (and (.-ctrlKey event)
                                (= (.-key event) "o")))
              key-handler (fn [event]
                            (when (key-match? event)
                              (if (str/includes? js/location.hash "dag")
                                (set! js/location.hash "")
                                (set! js/location.hash "dag"))))]
          (js/window.addEventListener "hashchange" hash-handler)
          (js/window.addEventListener "keydown" key-handler)
          (when (str/includes? js/location.hash "dag")
            (hash-handler))
          (get-dag-metadata storage-id
                            (fn [res err]
                              (if err
                                (js/console.error err)
                                (set-dag-metadata res))))
          (fn []
            (js/window.removeEventListener "keydown" key-handler)
            (js/window.removeEventListener "hashchange" hash-handler))))
      #js[])
    (when (and first-render (seq dag-metadata))
      [overlay* (merge props {:visible visible, :dag-metadata dag-metadata})])))

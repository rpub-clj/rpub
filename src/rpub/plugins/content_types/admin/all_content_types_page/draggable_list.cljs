(ns rpub.plugins.content-types.admin.all-content-types-page.draggable-list
  (:require ["react" :refer [useState useRef]]))

(defn- apply-drag-over [{:keys [items item-refs] :as _drag-state} event]
  (let [client-y (.-clientY event)
        positions (vec (map-indexed
                         (fn [index _]
                           (let [item (get (.-current item-refs) index)
                                 rect (.getBoundingClientRect item)]
                             {:index index
                              :top (.-top rect)
                              :bottom (.-bottom rect)
                              :middle (+ (.-top rect) (/ (.-height rect) 2))}))
                         items))
        before (< client-y (:top (first positions)))
        after (> client-y (:bottom (nth positions (dec (count positions)))))]
    (cond
      before 0
      after (count positions)

      :else
      (loop [i 0]
        (if (>= i (count positions))
          nil
          (let [curr-pos (get positions i)
                next-pos (get positions (inc i))
                next-item (and next-pos
                               (< client-y (:top next-pos))
                               (> client-y (:bottom curr-pos)))
                inside-item (and (>= client-y (:top curr-pos))
                                 (<= client-y (:bottom curr-pos)))
                top-half (< client-y (:middle curr-pos))]
            (cond
              next-item (inc i)
              inside-item (if top-half i (inc i))
              :else (recur (inc i)))))))))

(defn- apply-drop
  [{:keys [items drag-source drag-index drop-index] :as _drag-state}]
  (if drag-source
    (let [new-items (into-array items)]
      (.splice new-items drop-index 0 drag-source)
      new-items)
    (when (and drop-index (not= drag-index drop-index))
      (let [new-items (into-array items)
            [moved-item] (.splice new-items drag-index 1)
            adjusted-drop-index (if (> drop-index drag-index)
                                  (dec drop-index)
                                  drop-index)]
        (.splice new-items adjusted-drop-index 0 moved-item)
        new-items))))

(defn- draggable-item
  [{:keys [index item drag-state item-component on-drag-end on-drag-start]}]
  (let [{:keys [item-refs]} drag-state]
    [item-component
     {:item-data item
      :item-props {:ref (fn [node]
                          (let [item-refs' (assoc (.-current item-refs) index node)]
                            (set! (.-current item-refs) item-refs')))
                   :draggable true
                   :on-drag-start #(on-drag-start index)
                   :on-drag-end on-drag-end}}]))

(defn- top-divider?
  [{:keys [drag-source drag-index drop-index] :as _drag-state} index]
  (and (or (some? drag-index) drag-source) (= drop-index index)))

(defn- bottom-divider?
  [{:keys [drag-source drag-index drop-index items] :as _drag-state}]
  (and (or (some? drag-index) drag-source) (= drop-index (count items))))

(defn draggable-list
  [{:keys [drag-source items item-component divider-component on-items-change]}]
  (let [[drag-index set-drag-index] (useState nil)
        [drop-index set-drop-index] (useState nil)
        container-ref (useRef nil)
        item-refs (useRef [])
        drag-state {:items items
                    :item-refs item-refs
                    :drag-index drag-index
                    :drag-source drag-source
                    :drop-index drop-index}
        handle-drag-start (fn [index]
                            (set-drag-index index))
        handle-drag-over (fn [e]
                           (.preventDefault e)
                           (let [new-index (apply-drag-over drag-state e)]
                             (when (not= drop-index new-index)
                               (set-drop-index new-index))))
        handle-drop (fn [e]
                      (.preventDefault e)
                      (when-let [new-items (apply-drop drag-state)]
                        (on-items-change (seq new-items)))
                      (set-drag-index nil)
                      (set-drop-index nil))
        handle-drag-end (fn []
                          (set-drag-index nil)
                          (set-drop-index nil))]
    [:div {:ref container-ref
           :on-drag-over handle-drag-over
           :on-drop handle-drop}
     (map-indexed (fn [index item]
                    [:div {:key index}
                     (when (top-divider? drag-state index)
                       [divider-component])
                     [draggable-item
                      {:index index
                       :item item
                       :item-component item-component
                       :drag-state drag-state
                       :on-drag-end handle-drag-end
                       :on-drag-start handle-drag-start}]])
                  items)
     (when (bottom-divider? drag-state)
       [divider-component])]))

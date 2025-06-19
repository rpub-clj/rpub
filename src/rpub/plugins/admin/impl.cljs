(ns rpub.plugins.admin.impl
  {:no-doc true}
  (:require ["flowbite" :as flowbite]
            ["react" :refer [useId useEffect useState]]
            [clojure.string :as str]
            [rads.inflections :as inflections]
            [rpub.lib.dag :as dag]
            [rpub.lib.dag.react :refer [DAGProvider]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.lib.reagent :as r]))

(defn field-values
  ([db] (field-values db (keys (:inputs db))))
  ([db ks]
   (-> (:inputs db)
       (select-keys ks)
       (update-vals :value))))

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

(defonce dag-atom (atom nil))

(defonce client-id (random-uuid))

(defn ->unsaved-changes [key value]
  {:client-id client-id
   :key key
   :value value})

(defn update-unsaved-changes! [unsaved-changes]
  (let [http-opts' {:format :transit
                    :body unsaved-changes
                    :on-complete (fn [_ err]
                                   (when err
                                     (js/console.log err)))}]
    (http/post "/admin/api/update-unsaved-changes" http-opts')))

(defn table [{:keys [title rows header-buttons columns delete-row]}]
  (let [table-id (useId)]
    [:section {:class "bg-gray-50 antialiased"}
     [:div {:class "bg-white relative shadow-sm sm:rounded-lg overflow-hidden"}
      [:div {:class "flex flex-col md:flex-row items-center justify-between space-y-3 md:space-y-0 md:space-x-4 p-4"}
       [:div {:class "w-full md:w-1/2"}
        [:h2 {:class "text-3xl font-semibold font-app-serif"}
         title]]
       (when header-buttons
         [:div {:class "w-full md:w-auto flex flex-col md:flex-row space-y-2 md:space-y-0 items-stretch md:items-center justify-end md:space-x-3 flex-shrink-0"}
          header-buttons])]
      [:div {:class "overflow-x-auto"}
       [:table {:class "w-full text-sm text-left text-gray-500"}
        [:thead {:class "text-xs text-gray-700 uppercase bg-gray-50"}
         [:tr
          (for [column columns]
            [:td {:class "p-4" :scope "col" :key (:name column)}
             (:name column)])
          (when delete-row
            [:td {:class "px-4 py-3" :scope "col"}
             [:span {:class "sr-only"} "Actions"]])]]
        [:tbody
         (for [[row-index row] (map-indexed vector rows)]
           [:tr {:class (when (not= row-index 0)
                          "border-t border-gray-200")
                 :key (:id row)}
            (for [[i column] (map-indexed vector columns)]
              (if (= i 0)
                [:td {:class "px-4 py-3 font-medium text-gray-900 whitespace-nowrap"
                      :scope "row"
                      :key (:name column)}
                 ((:value column) row)]
                [:td {:class "px-4.py-3" :key (:name column)}
                 ((:value column) row)]))
            (when delete-row
              [:td {:class "px-4 py-3 flex items-center justify-end"}
               [:div
                [:button {:class "inline-flex items-center text-sm font-medium hover:bg-gray-100 p-1.5 text-center text-gray-500 hover:text-gray-800 rounded-lg focus:outline-none"
                          :type "button" :data-dropdown-toggle (str table-id "-actions-" (:id row))}
                 [:svg {:class "w-5.h-5" :aria-hidden "true" :fill "currentColor" :viewbox "0 0 20 20" :xmlns "http://www.w3.org/2000/svg"}
                  [:path {:d "M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z"}]]]
                [:div {:class "hidden z-10 w-30 bg-white rounded divide-y divide-gray-100 shadow"
                       :id (str table-id "-actions-" (:id row))}
                 [:ul {:class "py-1.text-sm" :aria-labelledby "benq-ex2710q-dropdown-button"}
                  [:li
                   [:button {:class "flex w-full items-center py-2 px-4 hover:bg-gray-100 text-red-500"
                             :type "button"
                             :on-click #(delete-row % row)}
                    [:svg {:class "w-4 h-4 mr-2" :viewbox "0 0 14 15" :fill "none" :xmlns "http://www.w3.org/2000/svg" :aria-hidden "true"}
                     [:path {:fill-rule "evenodd" :clip-rule "evenodd" :fill "currentColor" :d "M6.09922 0.300781C5.93212 0.30087 5.76835 0.347476 5.62625 0.435378C5.48414 0.523281 5.36931 0.649009 5.29462 0.798481L4.64302 2.10078H1.59922C1.36052 2.10078 1.13161 2.1956 0.962823 2.36439C0.79404 2.53317 0.699219 2.76209 0.699219 3.00078C0.699219 3.23948 0.79404 3.46839 0.962823 3.63718C1.13161 3.80596 1.36052 3.90078 1.59922 3.90078V12.9008C1.59922 13.3782 1.78886 13.836 2.12643 14.1736C2.46399 14.5111 2.92183 14.7008 3.39922 14.7008H10.5992C11.0766 14.7008 11.5344 14.5111 11.872 14.1736C12.2096 13.836 12.3992 13.3782 12.3992 12.9008V3.90078C12.6379 3.90078 12.8668 3.80596 13.0356 3.63718C13.2044 3.46839 13.2992 3.23948 13.2992 3.00078C13.2992 2.76209 13.2044 2.53317 13.0356 2.36439C12.8668 2.1956 12.6379 2.10078 12.3992 2.10078H9.35542L8.70382 0.798481C8.62913 0.649009 8.5143 0.523281 8.37219 0.435378C8.23009 0.347476 8.06631 0.30087 7.89922 0.300781H6.09922ZM4.29922 5.70078C4.29922 5.46209 4.39404 5.23317 4.56282 5.06439C4.73161 4.8956 4.96052 4.80078 5.19922 4.80078C5.43791 4.80078 5.66683 4.8956 5.83561 5.06439C6.0044 5.23317 6.09922 5.46209 6.09922 5.70078V11.1008C6.09922 11.3395 6.0044 11.5684 5.83561 11.7372C5.66683 11.906 5.43791 12.0008 5.19922 12.0008C4.96052 12.0008 4.73161 11.906 4.56282 11.7372C4.39404 11.5684 4.29922 11.3395 4.29922 11.1008V5.70078ZM8.79922 4.80078C8.56052 4.80078 8.33161 4.8956 8.16282 5.06439C7.99404 5.23317 7.89922 5.46209 7.89922 5.70078V11.1008C7.89922 11.3395 7.99404 11.5684 8.16282 11.7372C8.33161 11.906 8.56052 12.0008 8.79922 12.0008C9.03791 12.0008 9.26683 11.906 9.43561 11.7372C9.6044 11.5684 9.69922 11.3395 9.69922 11.1008V5.70078C9.69922 5.46209 9.6044 5.23317 9.43561 5.06439C9.26683 4.8956 9.03791 4.80078 8.79922 4.80078Z"}]] "Delete"]]]]]])])]]]]]))

(defn box [{:keys [title content class size selected on-drag-over on-drop
                   on-click hover]}]
  [:section {:class (str "bg-gray-50 antialiased " class)
             :on-click on-click
             :on-drag-over on-drag-over
             :on-drop on-drop}
   [:div {:class (str "h-full border bg-white relative shadow-sm sm:rounded-lg md:overflow-auto "
                      (if selected "ring-2 ring-blue-400 border-blue-500" "border-white") " "
                      (when hover "hover:border-blue-500"))}
    (when title
      [:div {:class "flex flex-col md:flex-row items-center justify-between space-y-3 md:space-y-0 md:space-x-4 p-6"}
       [:div {:class (str "w-full " (when (= size :half) "md:w-1/2"))}
        [:h2 {:class "text-3xl font-semibold font-app-serif"}
         title]]])
    (when content
      [:div {:class (str "md:overflow-auto p-6 "
                         (when title "pt-0"))}
       content])]])

(defn pluralize [coll word]
  (-> (inflections/pluralize (count coll) (inflections/singular word))
      (str/split #" ")))

(defn- content-item-count-text [{:keys [content-type]}]
  (inflections/pluralize
    (:content-item-count content-type)
    (inflections/singular (:name content-type))))

(defn content-item-counts [{:keys [content-types]}]
  [:ul
   (for [[i content-type] (map-indexed vector (sort-by :name content-types))]
     [:span {:key (:id content-type)}
      (when-not (= i 0)
        [:span {:class "text-gray-300"} " • "])
      [:a {:class "underline text-nowrap font-semibold"
           :href (str "/admin/content-types/" (name (:slug content-type)))}
       [content-item-count-text {:content-type content-type}]]])])

(defn index-by [f coll]
  (->> coll
       (map (fn [v] [(f v) v]))
       (into {})))

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

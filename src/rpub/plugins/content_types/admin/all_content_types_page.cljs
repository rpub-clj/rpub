(ns rpub.plugins.content-types.admin.all-content-types-page
  (:require ["react" :refer [useEffect]]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.dag :as dag]
            [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.content-types.admin.all-content-types-page.draggable-list
             :as draggable-list]))

(defn ->unsaved-changes [value]
  (admin-impl/->unsaved-changes
    :all-content-types-page
    (select-keys value [::content-types-index
                        ::past
                        ::future])))

(defn update-unsaved-changes! [value]
  (admin-impl/update-unsaved-changes! (->unsaved-changes value)))

(defn select-content-type [db {:keys [content-type]}]
  (let [selection {:content-type (select-keys content-type [:id])}]
    (assoc db ::selection selection)))

(defn select-content-type-field [db {:keys [content-type content-type-field]}]
  (let [selection {:content-type (select-keys content-type [:id])
                   :content-type-field (select-keys content-type-field [:id])}]
    (assoc db ::selection selection)))

(defn clear-selection [db]
  (dissoc db ::selection))

(defn selection [db]
  (let [content-types-index (get-in db [::dag/values ::content-types-index])
        sel (get db ::selection)
        content-type-id (get-in sel [:content-type :id])
        content-type-field-id (get-in sel [:content-type-field :id])
        content-type (get content-types-index content-type-id)]
    (cond-> nil
      (:content-type sel)
      (assoc :content-type content-type)

      (:content-type-field sel)
      (assoc :content-type-field
             (->> (:fields content-type)
                  (filter #(= (:id %) content-type-field-id))
                  first)))))

(defn drag-start [db drag-source]
  (assoc db ::drag-source drag-source))

(defn undo [db]
  (if (seq (::past db))
    (let [prev-state (peek (::past db))
          current-state (dissoc db ::past ::future)]
      (-> prev-state
          (assoc ::past (pop (::past db)))
          (assoc ::future ((fnil conj []) (::future db) current-state))))
    db))

(defn redo [db]
  (if (seq (::future db))
    (let [next-state (peek (::future db))
          current-state (dissoc db ::past ::future)]
      (-> next-state
          (assoc ::past ((fnil conj []) (::past db) current-state))
          (assoc ::future (pop (::future db)))))
    db))

(def undo-keys [::content-types-index])

(defn with-undo [push-fn]
  (fn [db & args]
    (let [current-state (select-keys db undo-keys)
          new-db (apply push-fn db args)]
      (-> new-db
          (update ::past (fnil conj []) current-state)
          (assoc ::future [])))))

(defn reset [{:keys [::original-content-types] :as db}]
  (let [content-types-index (admin-impl/index-by :id original-content-types)]
    (assoc db ::content-types-index content-types-index)))

(defn request-save [db]
  (assoc db ::pending-save true))

(defn confirm-save [db]
  (assoc db ::pending-save false))

(defn cancel-save [db]
  (assoc db ::pending-save false))

(defn content-type-fields-item
  [{:keys [content-type content-type-field field-types]}]
  (let [[{:keys [::selection]} push] (use-dag [::selection])
        selected (and (= (get-in selection [:content-type :id])
                         (:id content-type))
                      (= (get-in selection [:content-type-field :id])
                         (:id content-type-field)))
        field-type (get field-types (:type content-type-field))]
    [:div {:class (str "bg-white my-2 flex items-center"
                       "w-full font-semibold border "
                       "rounded-[6px] hover:border-blue-500 "
                       (if selected
                         "ring-2 ring-blue-400 border-blue-500"
                         "border-gray-100"))
           :data-content-type-field-id (:id content-type-field)
           :key (:id content-type-field)}
     [:label {:for "field-name"}]
     [:div.w-full.pl-2.py-2
      {:on-click (fn [_]
                   (push ::select-content-type-field
                         {:content-type content-type
                          :content-type-field content-type-field}))}
      (:name content-type-field)]
     [:div.bg-gray-50.px-2.py-2.border-l.border-l-gray-100
      {:class "min-w-[150px] rounded-r-[6px]"
       :on-click (fn [_]
                   (push ::select-content-type-field
                         {:content-type content-type
                          :content-type-field content-type-field}))}
      [(:label field-type)]]]))

(defn init [_ {:keys [content-types unsaved-changes]}]
  (let [content-types-index (admin-impl/index-by :id content-types)]
    {::past (get-in unsaved-changes [:value ::past])
     ::future (get-in unsaved-changes [:value ::future])
     ::original-content-types content-types
     ::content-types-index (or (get-in unsaved-changes [:value ::content-types-index])
                               content-types-index)}))

(defn field
  [{:keys [label description type selected draggable on-click] :as _props}]
  (let [[_ push] (use-dag)]
    (if-not draggable
      [:div.mb-2.flex.items-center.ps-4.border.border-gray-200
       {:class "rounded-[6px]"
        :on-click on-click}
       [:input {:class "w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 focus:ring-2"}
        {:type "radio" :checked selected :value "" :name "bordered-radio"}]
       [:label.w-full.py-4.ms-2.text-sm.font-medium.text-gray-900
        label]]
      [:div {:class (str "border rounded-[6px] p-2 mb-4 bg-gray-50 "
                         (if draggable "cursor-move hover:shadow-md hover:bg-gray-100 border-gray-100" "cursor-pointer hover:border-blue-500")
                         " "
                         (if selected "ring-2 ring-blue-400 border-blue-500" "border-gray-100"))
             :draggable draggable
             :on-drag-start (fn [_e]
                              (push ::drag-start {:id (random-uuid)
                                                  :name "New Field"
                                                  :type type}))}
       [:h4 {:class "font-semibold"}
        label]
       [:p {:class "text-sm text-gray-500"}
        description]])))

(defn inspector [{:keys [field-config]}]
  (let [[{:keys [::selection]}
         push] (use-dag [::selection ::content-types-index ::past ::future])
        change-field-type (fn [_e opts]
                            (-> (push ::change-field-type opts)
                                update-unsaved-changes!))
        delete-content-type (fn [_e opts]
                              (-> (push ::delete-content-type opts)
                                  update-unsaved-changes!))
        rename-content-type (fn [_e opts]
                              (when-let [new-name (js/prompt "New name:")]
                                (-> (push ::rename-content-type
                                          (assoc opts :new-name new-name))
                                    update-unsaved-changes!)))
        delete-content-type-field (fn [_e opts]
                                    (-> (push ::delete-content-type-field opts)
                                        update-unsaved-changes!))
        rename-content-type-field (fn [_e opts]
                                    (when-let [new-name (js/prompt "New name:")]
                                      (-> (push ::rename-content-type-field
                                                (assoc opts :new-name new-name))
                                          update-unsaved-changes!)))]
    (if selection
      (cond
        (:content-type-field selection)
        [admin-impl/box
         {:class "h-full"
          :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                  [:span.italic.text-blue-600 "Field: "]
                  (get-in selection [:content-type-field :name])]
          :content [:div
                    [:div.mb-8.flex
                     [html/action-button
                      {:class "mt-2 mr-2"
                       :on-click #(rename-content-type-field % selection)}
                      "Rename"]
                     [html/delete-button
                      {:class "mt-2"
                       :on-click #(delete-content-type-field % selection)}
                      "Delete"]]
                    [:h4 {:class "text-xl font-app-serif font-semibold mb-4"}
                     "Change Type"]
                    [:div
                     (for [n field-config]
                       [field (merge n {:selected (= (get-in selection [:content-type-field :type])
                                                     (:type n))
                                        :on-click #(change-field-type % (assoc selection :new-field-type (:type n)))})])]]}]
        (:content-type selection)
        [admin-impl/box
         {:class "h-full"
          :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                  [:span.italic.text-blue-600 "Content Type: "]
                  (get-in selection [:content-type :name])]
          :content [:div
                    [:div.mb-8.flex
                     [html/action-button
                      {:class "mt-2 mr-2"
                       :on-click #(rename-content-type % selection)}
                      "Rename"]
                     [html/delete-button
                      {:class "mt-2"
                       :on-click #(delete-content-type % selection)}
                      "Delete"]]
                    [:h4 {:class "text-xl font-app-serif font-semibold mb-4"}
                     "Add Field"]
                    [:div
                     (for [n field-config]
                       [field (assoc n :draggable true)])]]}])
      [admin-impl/box
       {:class "h-full"
        :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                "Add Field"]
        :content [:div
                  (for [n field-config]
                    [field (assoc n :draggable true)])]}])))

(defn ->content-type [{:keys [name]}]
  {:id (random-uuid)
   :slug :new-content-type
   :name name
   :content-item-count 0
   :fields [{:id (uuid "cd334826-1ec6-4906-8e7f-16ece1865faf")
             :name "Title",
             :type :text,
             :rank 1}
            {:id (uuid "6bd0ff7a-b720-4972-b98a-2aa85d179357")
             :name "Slug",
             :type :text,
             :rank 2}
            {:id (uuid "65a6aa2e-73a3-4283-afe1-58e610d6727d")
             :name "Content",
             :type :text-lg,
             :rank 3}]})

(defn header [{:keys [content-types]}]
  (let [[{:keys [::content-types-index]}
         push] (use-dag [::content-types-index ::past ::future])]
    [admin-impl/box
     {:class "pb-4"
      :data-no-select true
      :title [:div {:class "flex items-center"}
              [:div.grow "Content Types"]
              [html/button
               {:class "font-app-sans ml-auto"
                :on-click (fn [_]
                            (let [placeholder-name "New Content Type"
                                  new-name (loop [i 1
                                                  xs (vals content-types-index)]
                                             (let [n (if (= i 1)
                                                       placeholder-name
                                                       (str placeholder-name " " i))]
                                               (if (some #(= (:name %) n) xs)
                                                 (recur (inc i) (rest xs))
                                                 n)))]
                              (-> (push ::new-content-type
                                        {:content-type (->content-type {:name new-name})})
                                  update-unsaved-changes!)))}

               "New Content Type"]]
      :content [admin-impl/content-item-counts {:content-types content-types}]}]))

(defn update-content-types! [params]
  (let [body (select-keys params [:content-types])]
    (http/post "/admin/api/content-types/update" {:body body})))

(defn save-modal []
  (let [[{:keys [::pending-save ::content-types-index]}
         push] (use-dag [::pending-save ::content-types-index])]
    [html/modal
     {:visible pending-save
      :title "Save Changes"
      :content [:div {:class "p-4 space-y-4 md:p-5"}
                [:p.text-base.leading-relaxed.text-gray-500
                 "Are you sure you want to save your changes?"]
                [:pre (with-out-str (prn content-types-index))]]
      :on-confirm ^:async (fn [_]
                            (push ::confirm-save)
                            (js/await (update-content-types! {:content-types (vals content-types-index)})))
      :on-cancel (fn [_] (push ::cancel-save))}]))

(defn controls [{:keys [unsaved-changes]}]
  (let [[_ push] (use-dag [::content-types-index ::past ::future])]
    [admin-impl/box
     {:class "mb-4"
      :content [:div
                [:div.mb-4
                 [:div.flex.mb-2
                  [html/button
                   {:class "mr-2"
                    :on-click (fn [_]
                                (-> (push ::undo)
                                    update-unsaved-changes!))}
                   "Undo"]
                  [html/button
                   {:class "mr-2"
                    :on-click (fn [_]
                                (-> (push ::redo)
                                    update-unsaved-changes!))}
                   "Redo"]
                  [html/button
                   {:class "mr-2"
                    :on-click (fn [_]
                                (-> (push ::reset)
                                    update-unsaved-changes!))}
                   "Reset"]
                  [html/button
                   {:class "mr-2"
                    :on-click (fn [_] (push ::request-save))}
                   "Save"]]]
                [:div.text-lg.font-app-serif.font-semibold
                 [:span.italic.text-gray-400 "Status: "]
                 (if (seq (:value unsaved-changes))
                   "Unsaved changes."
                   "All changes saved.")]]}]))

(defn content-type-fields-list [{:keys [content-type field-types]}]
  (let [[{:keys [::drag-source]}
         push] (use-dag [::content-types-index ::selection ::past ::future
                         ::drag-source])]
    [draggable-list/draggable-list
     {:drag-source drag-source
      :items (sort-by :rank (:fields content-type))
      :divider-component (fn [_]
                           [:div {:class "h-[3px] bg-[#2196F3] my-[8px] rounded-[3px]"}])
      :item-component (fn [{:keys [item-props item-data]}]
                        [:div item-props
                         [content-type-fields-item
                          {:content-type content-type
                           :field-types field-types
                           :content-type-field item-data}]])
      :on-items-change (fn [new-fields]
                         (let [new-fields' (map-indexed (fn [i field] (assoc field :rank (inc i)))
                                                        new-fields)]
                           (-> (push ::update-content-type-fields
                                     {:content-type content-type
                                      :new-fields new-fields'})
                               update-unsaved-changes!)))}]))

(defn content-types-list [{:keys [field-types] :as _props}]
  (let [[{:keys [::content-types-index ::selection]}
         push] (use-dag [::content-types-index ::selection ::past ::future])
        content-types (->> (vals content-types-index) (sort-by :created-at >))]
    (for [content-type (sort-by :name content-types)]
      [:div {:class "pb-4"
             :key (:id content-type)
             :data-no-select true}
       [admin-impl/box
        {:hover true
         :on-click (fn [e]
                     (when-not (.closest (.-target e) "[data-content-type-field-id]")
                       (push ::select-content-type
                             {:content-type content-type})))
         :on-drag-over (fn [e]
                         (.preventDefault e))
         :on-drop (fn [_e]
                    #_(when (seq (.getData (.-dataTransfer e) "application/transit+json"))
                        (-> (push ::drag-drop {:content-type content-type})
                            update-unsaved-changes!)))
         :selected (and (not (:content-type-field selection))
                        (= (get-in selection [:content-type :id])
                           (:id content-type)))
         :title [:div {:class "flex items-center group"}
                 [:h3 {:class "text-2xl"}
                  (:name content-type)]]
         :content (when (seq (:fields content-type))
                    [:div {:key (:id content-type)}
                     [:div {:class "flex items-start"}]
                     [content-type-fields-list
                      {:field-types field-types
                       :content-type content-type}]])}]])))

(defn page [{:keys [field-types unsaved-changes] :as props}]
  (let [[{:keys [::content-types-index]} push] (use-dag [::content-types-index])
        _ (useEffect (fn [] (push ::init props)) #js[])
        clear? (fn [e]
                 (not (or (.closest (.-target e) "[data-no-select]")
                          (.closest (.-target e) "[data-content-type-field-id]"))))
        on-click (fn [e]
                   (when (clear? e)
                     (push ::clear-selection)))
        field-config (map (fn [[k v]] (assoc v :type k)) field-types)
        content-types (->> (vals content-types-index) (sort-by :created-at >))]
    (list
      [save-modal]
      [:div.flex {:class "flex"
                  :on-click on-click}
       [:div {:class "flex-grow"}
        [:div {:class "p-4 pr-[384px]"}
         [header {:content-types content-types}]
         [content-types-list {:content-types content-types
                              :field-types field-types
                              :unsaved-changes unsaved-changes}]]
        [:div {:class "p-4 pl-2 w-[376px] fixed right-0 bottom-0 top-12"
               :data-no-select true}
         [controls {:unsaved-changes unsaved-changes}]
         [inspector {:field-config field-config}]]]])))

(defn delete-content-type [db {:keys [content-type]}]
  (-> db
      (update ::content-types-index
              dissoc
              (:id content-type))
      (dissoc ::selection)))

(defn delete-content-type-field
  [db {:keys [content-type content-type-field]}]
  (let [fields-path [::content-types-index
                     (:id content-type)
                     :fields]]
    (-> db
        (update-in fields-path
                   (fn [fields]
                     (remove #(= (:id %) (:id content-type-field)) fields)))
        (dissoc ::selection))))

(defn rename-content-type [db {:keys [content-type new-name]}]
  (let [name-path [::content-types-index
                   (:id content-type)
                   :name]]
    (-> db
        (assoc-in name-path new-name)
        #_(dissoc ::selection))))

(defn rename-content-type-field
  [db {:keys [content-type content-type-field new-name]}]
  (let [fields-path [::content-types-index
                     (:id content-type)
                     :fields]]
    (-> db
        (update-in fields-path
                   (fn [fields]
                     (map (fn [field]
                            (if (= (:id field) (:id content-type-field))
                              (assoc field :name new-name)
                              field))
                          fields)))
        #_(dissoc ::selection))))

(defn- new-content-type
  [{:keys [::content-types-index] :as db}
   {:keys [content-type]}]
  (let [content-types-index' (-> content-types-index
                                 (assoc (:id content-type) content-type))]
    (assoc db ::content-types-index content-types-index')))

(defn- change-field-type
  [{:keys [::content-types-index] :as db}
   {:keys [content-type content-type-field new-field-type]}]
  (let [content-type' (-> (get content-types-index (:id content-type))
                          (update :fields
                                  (fn [fields]
                                    (map (fn [field]
                                           (if (= (:id field) (:id content-type-field))
                                             (assoc field :type new-field-type)
                                             field))
                                         fields))))
        content-types-index' (-> content-types-index
                                 (assoc (:id content-type) content-type'))]
    (assoc db ::content-types-index content-types-index')))

(defn- update-content-type-fields
  [{:keys [::content-types-index] :as db}
   {:keys [content-type new-fields]}]
  (let [content-type' (-> (get content-types-index (:id content-type))
                          (assoc :fields new-fields))
        content-types-index' (-> content-types-index
                                 (assoc (:id content-type) content-type'))]
    (-> db
        (assoc ::content-types-index content-types-index')
        (dissoc ::drag-source))))

(def dag-config
  {:nodes
   {::cancel-save {:push cancel-save}
    ::change-field-type {:push (with-undo change-field-type)}
    ::clear-selection {:push clear-selection}
    ::confirm-save {:push confirm-save}
    ::content-types-index {:calc ::content-types-index}
    ::delete-content-type {:push (with-undo delete-content-type)}
    ::delete-content-type-field {:push (with-undo delete-content-type-field)}
    ::drag-source {:calc ::drag-source}
    ::drag-start {:push drag-start}
    ::future {:calc ::future}
    ::init {:push init}
    ::new-content-type {:push (with-undo new-content-type)}
    ::past {:calc ::past}
    ::pending-save {:calc ::pending-save}
    ::redo {:push redo}
    ::update-content-type-fields {:push (with-undo update-content-type-fields)}
    ::rename-content-type {:push (with-undo rename-content-type)}
    ::rename-content-type-field {:push (with-undo rename-content-type-field)}
    ::request-save {:push request-save}
    ::reset {:push (with-undo reset)}
    ::select-content-type {:push select-content-type}
    ::select-content-type-field {:push select-content-type-field}
    ::selection {:calc selection}
    ::undo {:push undo}}

   :edges
   [[::cancel-save ::pending-save]
    [::change-field-type ::content-types-index]
    [::change-field-type ::future]
    [::change-field-type ::past]
    [::clear-selection ::selection]
    [::confirm-save ::pending-save]
    [::content-types-index ::selection]
    [::delete-content-type ::content-types-index]
    [::delete-content-type ::future]
    [::delete-content-type ::past]
    [::delete-content-type-field ::content-types-index]
    [::delete-content-type-field ::future]
    [::delete-content-type-field ::past]
    [::drag-start ::drag-source]
    [::init ::content-types-index]
    [::new-content-type ::content-types-index]
    [::new-content-type ::future]
    [::new-content-type ::past]
    [::redo ::content-types-index]
    [::redo ::future]
    [::redo ::past]
    [::rename-content-type ::content-types-index]
    [::rename-content-type ::future]
    [::rename-content-type ::past]
    [::rename-content-type-field ::content-types-index]
    [::rename-content-type-field ::future]
    [::rename-content-type-field ::past]
    [::update-content-type-fields ::content-types-index]
    [::update-content-type-fields ::drag-source]
    [::update-content-type-fields ::future]
    [::update-content-type-fields ::past]
    [::request-save ::pending-save]
    [::reset ::content-types-index]
    [::reset ::future]
    [::reset ::past]
    [::select-content-type ::selection]
    [::select-content-type-field ::selection]
    [::undo ::content-types-index]
    [::undo ::future]
    [::undo ::past]]})

(def config
  {:page-id :all-content-types-page
   :component page
   :dag-config dag-config})

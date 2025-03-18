(ns rpub.plugins.content-types
  (:require ["react" :refer [useEffect]]
            [rads.inflections :as inflections]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]))

(defn ->field [{:keys [name type]}]
  {:id (random-uuid)
   :name name
   :type type})

(defn ->content-type [{:keys [id name slug fields]}]
  {:id (or (random-uuid) id)
   :name name
   :slug slug
   :fields fields
   :created-at (js/Date.)
   :content-item-count 0})

(defn- field-type-label [field]
  (case (:type field)
    :text "Text"
    :text-lg "Text (Large)"
    (name (:type field))))

(defn content-type-fields-form [{:keys [anti-forgery-token content-type class]}]
  (let [[{:keys [:all-content-types-page/selection]}
         push] (use-dag [:all-content-types-page/selection])
        _http-opts {:format :transit}
        #_#_update-field (useCallback
                           (fn [content-type-id field-key]
                             (html/debounce
                               (fn [e _content-type field]
                                 (.preventDefault e)
                                 (let [updated-field (assoc field field-key (-> e .-target .-value))
                                       http-opts' (assoc http-opts :body (merge {:content-type-id content-type-id
                                                                                 :content-field-id (:id updated-field)}
                                                                                (select-keys updated-field [:name :type :rank])))]
                                   (http/post "/api/update-content-type-field" http-opts')
                                   #_(set-state (update-in state [:content-type-index (:id content-type) :fields]
                                                           (fn [fields]
                                                             (map (fn [field]
                                                                    (if (= (:id field) (:id updated-field))
                                                                      updated-field
                                                                      field))
                                                                  fields))))))
                               html/default-debounce-timeout-ms)))
        #_#_delete-field (fn [e content-type field]
                           (.preventDefault e)
                           (when (js/confirm (str "Are you sure you want to delete \""
                                                  (:name field)
                                                  "\"?"))
                             (let [http-opts' (assoc http-opts :body {:content-type-id (:id content-type)
                                                                      :content-field-id (:id field)})]
                               (http/post "/api/delete-content-type-field" http-opts')
                               #_(set-state (update-in state [:content-type-index (:id content-type) :fields]
                                                       (fn [fields]
                                                         (remove #(= (:id %) (:id field)) fields)))))))
        #_#_update-field-name (update-field (:id content-type) :name)
        #_#_update-field-type (update-field (:id content-type) :type)]
    [:form {:method "post" :class class}
     [:input {:id "__anti-forgery-token"
              :name "__anti-forgery-token"
              :type "hidden"
              :value anti-forgery-token}]
     [:input {:type "hidden" :name "content-type-id" :value (:id content-type)}]
     [:input {:type "hidden" :name "content-type-name" :value (:name content-type)}]
     [:div
      (for [field (sort-by :rank (:fields content-type))
            :let [selected (and (= (get-in selection [:content-type :id])
                                   (:id content-type))
                                (= (get-in selection [:content-type-field :id])
                                   (:id field)))]]
        [:div {:class (str "my-2 flex items-center"
                           "w-full font-semibold border "
                           "rounded-[6px] hover:border-blue-500 "
                           (if selected
                             "ring-2 ring-blue-400 border-blue-500"
                             "border-gray-200"))
               :data-content-type-field-id (:id field)
               :key (:id field)}
         [:label {:for "field-name"}]
         [:div.w-full.pl-2.py-2
          {:onClick (fn [_]
                      (push :all-content-types-page/select-content-type-field
                            {:content-type content-type
                             :content-type-field field}))}
          (:name field)]
         [:div.bg-gray-50.px-2.py-2.border-l.border-l-gray-200
          {:class "min-w-[150px] rounded-r-[6px]"
           :onClick (fn [_]
                      (push :all-content-types-page/select-content-type-field
                            {:content-type content-type
                             :content-type-field field}))}
          [field-type-label field]]
         #_[html/button
            {:on-click #(delete-field % content-type field)
             :class "opacity-0 group-hover:opacity-100 transition duration-75"
             :color :red}
            "Delete Field"]])]]))

(def field-config
  [{:label "Text"
    :description "Ask for text with optional formatting."
    :type :text}
   {:label "Date and Time"
    :description "Ask for a date and time with a date picker."
    :type :datetime}
   {:label "Number"
    :description "Ask for a whole number or a decimal."
    :type :number}
   {:label "Media"
    :description "Ask for an image or video."
    :type :media}
   {:label "Choice"
    :description "Ask for a choice between multiple options."
    :type :choice}
   {:label "Group"
    :description "Combine multiple fields into a group."
    :type :group}])

(defn all-content-types-page [props]
  (let [[{:keys [:all-content-types-page/selection
                 :model/content-types-index]}
         push] (use-dag [:all-content-types-page/selection
                         :model/content-types-index])
        _ (useEffect #(push :init (select-keys props [:content-types])) #js[])
        _http-opts {:format :transit}
        #_#_set-content-type-name (useCallback
                                    (html/debounce
                                      (fn [e content-type]
                                        (let [value (-> e .-target .-value)
                                              content-type' (-> (assoc content-type :name value)
                                                                (select-keys [:id :name]))
                                              http-opts' (assoc http-opts
                                                                :body
                                                                {:content-type content-type'})]
                                          (http/post "/api/update-content-type" http-opts')
                                          #_(set-state (assoc-in state [:content-type-index (:id content-type) :name] value))))
                                      html/default-debounce-timeout-ms))
        #_#_new-content-type (fn [e]
                               (let [content-type (->content-type
                                                    {:name "New Content Type"
                                                     :slug "new-content-type"
                                                     :fields []})]
                                 (.preventDefault e)
                                 (http/post "/api/new-content-type" http-opts)
                                 #_(set-state (assoc-in state [:content-type-index (:id content-type)] content-type))))
        #_#_delete-content-type (fn [e content-type]
                                  (when (js/confirm (str "Are you sure you want to delete \""
                                                         (:name content-type)
                                                         "\"?"))
                                    (let [http-opts' (assoc http-opts
                                                            :body
                                                            {:content-type-id (:id content-type)})]
                                      (.preventDefault e)
                                      (http/post "/api/delete-content-type" http-opts')
                                      #_(set-state (update state :content-type-index #(dissoc % (:id content-type)))))))
        #_#_new-field (fn [e content-type field]
                        (.preventDefault e)
                        (let [rank (inc (apply max 0 (map :rank (:fields content-type))))
                              field' (-> (->field field) (assoc :rank rank))
                              http-opts' (assoc http-opts :body {:content-type-id (:id content-type)})]
                          (http/post "/api/new-content-type-field" http-opts')
                          #_(set-state (update-in state [:content-type-index (:id content-type) :fields] conj field'))))
        delete-content-type-field (fn [_e field]
                                    (println
                                      (js/confirm
                                        (str "Are you sure you want to delete the \""
                                             (:name field)
                                             "\" field?"))))
        content-types (->> (vals content-types-index) (sort-by :created-at >))]
    [:div {:class "flex"
           :onClick (fn [e]
                      (when-not (or (.closest (.-target e) "[data-no-select]")
                                    (.closest (.-target e) "[data-content-type-field-id]"))
                        (push :all-content-types-page/clear-selection)))}
     [:div {:class "flex-grow"}
      [:div {:class "p-4 pr-[384px]"}
       [admin-impl/box
        {:class "pb-4"
         :data-no-select true
         :title [:div {:class "flex items-center"}
                 [:div "Content Types"]
                 #_[html/button
                    {:class "font-app-sans ml-auto"
                     :on-click new-content-type}
                    "New Content Type"]]
         :content [admin-impl/content-item-counts {:content-types content-types}]}]
       (for [content-type content-types]
         [:div {:class "pb-4"
                :key (:id content-type)
                :data-no-select true}
          [admin-impl/box
           {:hover true
            :on-click (fn [e]
                        (when-not (.closest (.-target e) "[data-content-type-field-id]")
                          (push :all-content-types-page/select-content-type
                                {:content-type content-type})))
            :on-drag-over (fn [e]
                            (.preventDefault e)
                            #_(prn :drag-over e))
            :on-drop (fn [_e]
                       (push :all-content-types-page/drag-drop
                             {:content-type content-type}))
            :selected (and (not (:content-type-field selection))
                           (= (get-in selection [:content-type :id])
                              (:id content-type)))
            :title [:div {:class "flex items-center group"}
                    [:h3 {:class "text-2xl"}
                     (:name content-type)]
                    #_[html/input {:type :text
                                   :class "max-w-xl"
                                   :size :text-2xl
                                   :name :content-type-name
                                   :placeholder "Name"
                                   :on-focus (fn [_] (push :all-content-types-page/select-content-type (:id content-type)))
                                   :on-blur (fn [_] (push :all-content-types-page/clear-selection))
                                   #_#_:on-change #(set-content-type-name % content-type)
                                   :default-value (:name content-type)}]
                    #_[html/button
                       {:class "ml-5 font-app-sans"
                        :on-click #(new-field %
                                              content-type
                                              {:name "New Field", :type :text})}
                       "New Field"]
                    #_[html/button
                       {:class "font-app-sans ml-2 opacity-0 group-hover:opacity-100 transition duration-75"
                        :color :red
                        :on-click #(delete-content-type % content-type)}
                       "Delete Content Type"]]
            :content (when (seq (:fields content-type))
                       [:div {:key (:id content-type)}
                        [:div {:class "flex items-start"}]
                        [content-type-fields-form
                         {:content-type content-type}]])}]])]]
     (let [field (fn [{:keys [label description selected draggable] :as props}]
                   [:div {:class (str "border rounded-[6px] p-2 mb-4 bg-gray-100 "
                                      (if draggable "cursor-move hover:shadow-md border-gray-100" "cursor-pointer hover:border-blue-500")
                                      " "
                                      (if selected "ring-2 ring-blue-400 border-blue-500" "border-gray-100"))
                          :draggable draggable
                          :onDragStart (fn [e]
                                         (push :all-content-types-page/drag-start props)
                                         (println :onDragStart e))}
                    [:h4 {:class "font-semibold"}
                     label]
                    [:p {:class "text-sm text-gray-500"}
                     description]])]
       [:div {:class "p-4 pl-2 w-[376px] fixed right-0 bottom-0 top-12"
              :data-no-select true}
        (if selection
          (cond
            (:content-type-field selection)
            [admin-impl/box
             {:class "h-full"
              :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                      [:span.italic.text-blue-600 "Field: "]
                      (get-in selection [:content-type-field :name])]
              :content [:div
                        [:div.mb-8
                         [html/action-button
                          {:class "mt-2 mr-2"}
                          "Rename Field"]
                         [html/delete-button
                          {:class "mt-2"
                           :on-click #(delete-content-type-field % (:content-type-field selection))}
                          "Delete Field"]]
                        [:h4 {:class "text-xl font-app-serif font-semibold mb-4"}
                         "Change Field Type"]
                        [:div
                         (for [n field-config]
                           (field (merge n
                                         {:selected (= (get-in selection [:content-type-field :type])
                                                       (:type n))})))]]}]

            (:content-type selection)
            [admin-impl/box
             {:class "h-full"
              :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                      [:span.italic.text-blue-600 "Content Type: "]
                      (get-in selection [:content-type :name])]
              :content [:div
                        [:div.mb-8
                         [html/action-button
                          {:class "mt-2 mr-2"}
                          "Rename Content Type"]
                         [html/delete-button
                          {:class "mt-2"
                           :on-click #(delete-content-type-field % (:content-type-field selection))}
                          "Delete Content Type"]]
                        [:h4 {:class "text-xl font-app-serif font-semibold mb-4"}
                         "Add Field"]
                        [:div
                         (for [n field-config]
                           (field (assoc n :draggable true)))]]}])

          [admin-impl/box
           {:class "h-full"
            :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                    "Add Field"]
            :content [:div
                      #_[:ul {:class "text-sm mb-8 list-[disc] pl-4"}
                         [:li {:class "mb-2"} "Drag a field to the left to add it to a content type."]
                         [:li "Double-click a field to add it to the selected content type."]]
                      [:div
                       (for [n field-config]
                         (field (assoc n :draggable true)))]]}])])]))

(html/add-element :all-content-types-page
                  (admin-impl/wrap-component all-content-types-page)
                  {:format :transit})

(def months
  ["January" "February" "March" "April" "May" "June"
   "July" "August" "September" "October" "November" "December"])

(defn- format-datetime [date]
  (let [month-idx (.getMonth date)
        day (.getDate date)
        year (.getFullYear date)
        hours (.getHours date)
        minutes (.getMinutes date)
        month (get months month-idx)
        meridiem (if (< hours 12) "AM" "PM")
        h (mod hours 12)
        display-hours (if (zero? h) 12 h)
        display-minutes (if (< minutes 10) (str "0" minutes) (str minutes))]
    (str month " " day ", " year " " display-hours ":" display-minutes " " meridiem)))

(defn edit-content-item-path [content-item]
  (str "/admin/content-types/"
       (name (get-in content-item [:content-type :slug]))
       "/content-items/"
       (name (get-in content-item [:fields "Slug"]))))

(def columns
  [{:name "Title"
    :value (fn [{:keys [fields] :as content-item}]
             [:a.underline {:href (edit-content-item-path content-item)}
              [:span {:class "font-semibold"}
               (get fields "Title")]])}

   {:name "Author"
    :value
    (fn [{:keys [created-by]}]
      (let [{:keys [username]} created-by]
        [:span {:class "font-semibold"}
         username]))}

   {:name "Date"
    :value (fn [{:keys [created-at updated-at]}]
             (some-> (or updated-at created-at)
                     js/Date.
                     format-datetime))}])

(defn new-content-item-path [content-type]
  (str "/admin/content-types/"
       (name (:slug content-type))
       "/content-items/new"))

(defn single-content-type-page [{:keys [content-type content-items]}]
  (let [http-opts {:format :transit}
        content-items (map (fn [content-item]
                             (update content-item :fields #(update-keys % name)))
                           content-items)
        delete-row (fn [_ content-item]
                     (let [body {:content-item-id (:id content-item)}
                           on-complete (fn [_ err]
                                         (if err
                                           (println err)
                                           nil #_(set-state (update state :content-items
                                                                    (fn [content-items]
                                                                      (remove #(= (:id %) (:id content-item))
                                                                              content-items))))))
                           http-opts' (merge http-opts {:body body
                                                        :on-complete on-complete})]
                       (http/post "/api/delete-content-item" http-opts')))
        content-items' (->> content-items (map #(assoc % :content-type content-type)))]
    [:div {:class "p-4"}
     [admin-impl/table
      {:title (:name content-type)
       :columns columns
       :rows content-items'
       :header-buttons [:a {:href (new-content-item-path content-type)}
                        [html/action-button
                         (str "New " (inflections/singular (:name content-type)))]]
       :delete-row delete-row}]]))

(html/add-element :single-content-type-page
                  (admin-impl/wrap-component single-content-type-page)
                  {:format :transit})

(defn- content-item-fields [{:keys [content-item editing creating]}]
  (let [{:keys [content-type]} content-item
        document' (->> (:document content-item)
                       (sort-by (fn [[k _]]
                                  (let [fld (some #(when (= (:id %) k) %) (:fields content-type))]
                                    (:rank fld)))))]
    [:div
     (for [[k v] document'
           :let [field (as-> (some #(when (= (:id %) k) %) (:fields content-type)) $
                         (assoc $ :key (str (:id $))))
                 v' (if (= v ::new-field) "" v)]]
       [:div.max-w-xl.mb-4 {:key (:id field)}
        [:label.font-semibold.mb-1.block {:for (:name field)}
         (:name field)]
        [:div
         (case (:type field)
           :text [html/input2 (cond-> {:type :text
                                       :name (:key field)
                                       :on-change prn}
                                editing (assoc :value v')
                                creating (assoc :placeholder (field-type-label field)))]
           :text-lg [html/textarea (cond-> {:name (:key field)
                                            :on-change prn}
                                     editing (assoc :value v')
                                     creating (assoc :placeholder (field-type-label field)))]
           :choice [:select]
           :datetime [:input {:type :text}]
           :number [:input {:type :number}]
           (pr-str field))]])]))

(defn new-content-item-page [{:keys [content-type]}]
  (let [content-item {:content-type content-type
                      :document (->> (:fields content-type)
                                     (map (fn [field] [(:id field) ::new-field]))
                                     (into {}))}
        submitting false]
    [:div.p-4
     [admin-impl/box
      {:class "mb-4"
       :title (str "New " (inflections/singular (get-in content-item [:content-type :name])))}]
     [admin-impl/box
      {:content
       [:div
        [content-item-fields {:content-item content-item
                              :creating true}]
        [html/submit-button {:ready-label "Create"
                             :submit-label "Creating..."
                             :submitting submitting}]]}]]))

(html/add-element :new-content-item-page
                  (admin-impl/wrap-component new-content-item-page)
                  {:format :transit})

(defn edit-content-item-page [{:keys [content-type content-item]}]
  (let [submitting false]
    [:div.p-4
     [admin-impl/box
      {:class "mb-4"
       :title
       [:div
        [:span.italic.text-blue-600
         (str (inflections/singular (:name content-type)) ": ")]
        (get-in content-item [:fields "Title"])]}]
     [admin-impl/box
      {:content
       [:div
        [content-item-fields {:content-item content-item
                              :editing true}]
        [html/submit-button {:ready-label "Save"
                             :submit-label "Saving..."
                             :submitting submitting}]]}]]))

(html/add-element :edit-content-item-page
                  (admin-impl/wrap-component edit-content-item-page)
                  {:format :transit})

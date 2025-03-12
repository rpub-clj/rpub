(ns rpub.plugins.content-types
  (:refer-clojure :exclude [random-uuid])
  (:require ["react" :refer [useEffect useCallback]]
            [clojure.string :as str]
            [rads.inflections :as inflections]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]))

(defn random-uuid []
  (js/crypto.randomUUID))

(def quill-toolbar
  [[{:header [1 2 3 4 5 6 nil]}]
   ["bold" "italic"]
   [{:list "bullet"} {:list "ordered"}]
   ["blockquote"]
   [{:align ""} {:align "center"} {:align "right"}]
   ["link"]
   ["clean"]])

(def quill-defaults
  {:theme :snow
   :modules {:toolbar quill-toolbar}})

(defn start-quill! [selector opts]
  (let [opts' (merge quill-defaults (dissoc opts :html))
        instance (new js/Quill selector (clj->js opts'))]
    (when-let [html (:html opts)]
      (.dangerouslyPasteHTML (.-clipboard instance) html))
    instance))

(defn ->field [{:keys [name type]}]
  {:id (random-uuid)
   :name name
   :type type})

(defn ->slug [title]
  (inflections/parameterize title))

(defn ->content-type [{:keys [id name slug fields]}]
  {:id (or (random-uuid) id)
   :name name
   :slug slug
   :fields fields
   :created-at (js/Date.)
   :content-item-count 0})

(defn content-type-fields-form [{:keys [anti-forgery-token content-type class]}]
  (let [[{:keys [:all-content-types-page/selection]}
         push] (use-dag [:all-content-types-page/selection])
        http-opts {:anti-forgery-token anti-forgery-token}
        update-field (useCallback
                       (fn [content-type-id field-key]
                         (html/debounce
                           (fn [e content-type field]
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
        delete-field (fn [e content-type field]
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
        update-field-name (update-field (:id content-type) :name)
        update-field-type (update-field (:id content-type) :type)]
    [:form {:method "post" :class class}
     [:input {:id "__anti-forgery-token"
              :name "__anti-forgery-token"
              :type "hidden"
              :value anti-forgery-token}]
     [:input {:type "hidden" :name "content-type-id" :value (:id content-type)}]
     [:input {:type "hidden" :name "content-type-name" :value (:name content-type)}]
     [:div
      (for [field (sort-by :rank (:fields content-type))]
        [:div {:class "mb-2 pb-2 pt-2 flex items-center group"
               :key (:id field)}
         [:label {:for "field-name"}]
         [html/input
          {:type :text
           :on-focus (fn [_]
                       (push :all-content-types-page/select-content-type-field
                             {:content-type content-type
                              :content-type-field field}))
           #_#_:on-blur (fn [_] (push :all-content-types-page/clear-selection))
           :size :text-medium
           :class (str "px-2 py-1 font-semibold border border-gray-200 "
                       "rounded-[6px] mr-4 max-w-xl")
           :placeholder "Field Name"
           :name :field-name
           :readonly true
           #_#_:on-change #(update-field-name % content-type field)
           :default-value (:name field)}]
         [:label {:for "field-type"}]
         [html/select
          {:name :field-type
           :on-focus (fn [_]
                       (push :all-content-types-page/select-content-type-field
                             {:content-type content-type
                              :content-type-field field}))
           #_#_:on-blur (fn [_] (push :all-content-types-page/clear-selection))
           :default-value (:type field)
           #_#_:on-change #(update-field-type % content-type field)}
          [:option {:key :text :value "text"} "Text"]
          [:option {:key :text-lg :value "text-lg"} "Text (Large)"]
          [:option {:key :number :value "number"} "Number"]
          [:option {:key :choice :value "choice"} "Choice"]
          [:option {:key :datetime :value "datetime"} "Date/Time"]]
         #_[html/button
            {:on-click #(delete-field % content-type field)
             :class "opacity-0 group-hover:opacity-100 transition duration-75"
             :color :red}
            "Delete Field"]])]]))

(defn- index-by [f coll]
  (->> coll
       (map (fn [v] [(f v) v]))
       (into {})))

(def field-config
  [{:label "Text" :description "Ask for text with optional formatting."}
   {:label "Date and Time" :description "Ask for a date and time with a date picker."}
   {:label "Number" :description "Ask for a whole number or a decimal."}
   {:label "Media" :description "Ask for an image or video."}
   {:label "Choice" :description "Ask for a choice between multiple options."}
   {:label "Group" :description "Combine multiple fields into a group."}])

(defn all-content-types-page [{:keys [content-types anti-forgery-token]}]
  (let [[{:keys [:all-content-types-page/selection]}
         push] (use-dag [:all-content-types-page/selection])
        _ (useEffect #(push :init {:content-types content-types}) #js[])
        content-types (map #(update % :created-at js/Date.parse) content-types)
        content-type-index (admin-impl/index-by :id content-types)
        http-opts {:anti-forgery-token anti-forgery-token}
        set-content-type-name (useCallback
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
        new-content-type (fn [e]
                           (let [content-type (->content-type
                                                {:name "New Content Type"
                                                 :slug "new-content-type"
                                                 :fields []})]
                             (.preventDefault e)
                             (http/post "/api/new-content-type" http-opts)
                             #_(set-state (assoc-in state [:content-type-index (:id content-type)] content-type))))
        delete-content-type (fn [e content-type]
                              (when (js/confirm (str "Are you sure you want to delete \""
                                                     (:name content-type)
                                                     "\"?"))
                                (let [http-opts' (assoc http-opts
                                                        :body
                                                        {:content-type-id (:id content-type)})]
                                  (.preventDefault e)
                                  (http/post "/api/delete-content-type" http-opts')
                                  #_(set-state (update state :content-type-index #(dissoc % (:id content-type)))))))
        new-field (fn [e content-type field]
                    (.preventDefault e)
                    (let [rank (inc (apply max 0 (map :rank (:fields content-type))))
                          field' (-> (->field field) (assoc :rank rank))
                          http-opts' (assoc http-opts :body {:content-type-id (:id content-type)})]
                      (http/post "/api/new-content-type-field" http-opts')
                      #_(set-state (update-in state [:content-type-index (:id content-type) :fields] conj field'))))
        content-types (->> (vals content-type-index) (sort-by :created-at >))]
    [:div {:class "flex"
           :onClick (fn [e]
                      (when-not (.closest (.-target e) "[data-content-type-id]")
                        (push :all-content-types-page/clear-selection)))}
     [:div {:class "flex-grow"}
      [:div {:class "p-4 pr-[384px]"}
       [admin-impl/box
        {:class "pb-4"
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
                :data-content-type-id (:id content-type)}
          [admin-impl/box
           {:on-click (fn [e]
                        (when-not (or (.closest (.-target e) "input")
                                      (.closest (.-target e) "select"))
                          (push :all-content-types-page/select-content-type
                                {:content-type content-type})))
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
                         {:content-type content-type
                          :anti-forgery-token anti-forgery-token}]])}]])]]
     (let [field (fn [{:keys [label description]}]
                   [:div {:class "border border-gray-200 rounded-[6px] p-2 mb-4 bg-gray-50 cursor-move"
                          :draggable true}
                    [:h4 {:class "font-semibold"}
                     label]
                    [:p {:class "text-sm text-gray-500"}
                     description]])]
       [:div {:class "p-4 pl-2 w-[376px] fixed right-0 bottom-0 top-12"}
        (if selection
          (cond
            (:content-type-field selection)
            [admin-impl/box
             {:class "h-full"
              :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                      [:span {:class "italic"} "Field: "]
                      (get-in selection [:content-type-field :name])]
              :content [:div
                        [:div {:class "mb-8"}
                         [:h2 {:class "text-xl font-app-serif font-semibold"}
                          "Info"]]
                        [:div
                         [:h2 {:class "text-xl font-app-serif font-semibold"}
                          "Conditions"]
                         [:p "foo"]]]}]

            (:content-type selection)
            [admin-impl/box
             {:class "h-full"
              :title [:h3 {:class "text-2xl font-app-serif font-semibold"}
                      [:span {:class "italic"} "Content Type: "]
                      (get-in selection [:content-type :name])]
              :content [:div
                        [:h4 {:class "text-xl font-app-serif font-semibold mb-4"}
                         "Add Field"]
                        [:div
                         (for [n field-config]
                           (field n))]]}])

          [admin-impl/box
           {:class "h-full"
            :content [:div
                      [:ul {:class "text-sm mb-8 list-[disc] pl-4"}
                       [:li {:class "mb-2"} "Drag a field to the left to add it to a content type."]
                       [:li "Double-click a field to add it to the selected content type."]]
                      [:div
                       (for [n field-config]
                         (field n))]]}])])]))

(html/add-element :all-content-types-page (admin-impl/wrap-component all-content-types-page))

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

(def columns
  [{:name "Title"
    :value (fn [{:keys [fields]}]
             [:span {:class "font-semibold"}
              (get fields "Title")])}

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

(defn single-content-type-page [{:keys [content-type content-items anti-forgery-token]}]
  (let [http-opts {:anti-forgery-token anti-forgery-token}
        content-items (map (fn [content-item]
                             (update content-item :fields #(update-keys % name)))
                           content-items)
        delete-row (fn [_ content-item]
                     (let [body {:content-item-id (:id content-item)}
                           on-complete (fn [_ err]
                                         (if err
                                           (println err)
                                           #_(set-state (update state :content-items
                                                                (fn [content-items]
                                                                  (remove #(= (:id %) (:id content-item))
                                                                          content-items))))))
                           http-opts' (merge http-opts {:body body
                                                        :on-complete on-complete})]
                       (http/post "/api/delete-content-item" http-opts')))]
    [:div {:class "p-4"}
     [admin-impl/table
      {:title (:name content-type)
       :columns columns
       :rows (map #(assoc % :content-type content-type) content-items)
       :delete-row delete-row}]]))

(html/add-element :single-content-type-page (admin-impl/wrap-component single-content-type-page))

(defn editor-impl [{:keys [on-start] :as props}]
  #_(let [id (str (gensym))
          [started set-started!] (useState false)
          container-props (dissoc props :on-start)]
      (useEffect
        (fn []
          (when (not started)
            (when-let [container (js/document.getElementById id)]
              (let [quill-opts (dissoc props :on-start)
                    quill-instance (start-quill! container quill-opts)]
                (set-started! true)
                (on-start quill-instance))))
          nil)
        #js[started])
      [:div (merge container-props {:id id})]))

(defn editor [props]
  [:div {:class "editor bg-white"}
   [editor-impl props]])

(defn quill-get-semantic-html [^Quill quill]
  (.getSemanticHTML quill))

(def title-field-id (uuid "cd334826-1ec6-4906-8e7f-16ece1865faf"))
(def slug-field-id (uuid "6bd0ff7a-b720-4972-b98a-2aa85d179357"))

(defn success-alert [{:keys [message]}]
  [:div {:class "flex items-center p-4 mb-4 text-sm text-green-800 border border-green-300 rounded-lg bg-green-50 dark:bg-gray-800 dark:text-green-400 dark:border-green-800" :role "alert"}
   [:svg {:class "flex-shrink-0 inline w-4 h-4 me-3" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :fill "currentColor" :viewBox "0 0 20 20"}
    [:path {:d "M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"}]]
   [:span {:class "sr-only"} "Info"]
   [:div
    message]])

(defn content-type-new-item-form
  [{:keys [content-type
           content-item
           site-base-url
           anti-forgery-token
           title
           submit-button-text
           submit-button-class
           submit-form-url
           submitting-button-text]}]
  (let [http-opts {:anti-forgery-token anti-forgery-token}
        submitting false
        content-item {:form-fields (or (:document content-item) {})}
        messages []
        add-editor (fn [_field-id _e] #_(set-state (assoc-in state [:editors %1] %2)))
        add-message (fn [_message] #_(set-state (update state :messages conj %)))
        update-field (fn [e field-id]
                       (let [value (-> e .-target .-value)]
                         #_(set-state (assoc-in state [:content-item :form-fields field-id] value))))
        submit-form (fn [e {:keys [content-item-slug]}]
                      (.preventDefault e)
                      (let [v nil #_(assoc state :submitting true)
                            form-fields (get-in v [:content-item :form-fields])
                            editor-values (update-vals (:editors v) quill-get-semantic-html)
                            document (merge form-fields editor-values
                                            {slug-field-id content-item-slug})
                            body (cond-> {:content-type-id (:id content-type)
                                          :document document}
                                   content-item (assoc :content-item-id (:id content-item)))
                            on-complete (fn [res err]
                                          (println res)
                                          (if err
                                            (println err)
                                            (if content-item
                                              (add-message [success-alert
                                                            {:message
                                                             [:span.font-semibold
                                                              (inflections/capitalize (inflections/singular (name (:slug content-type)))) " updated!"]}])
                                              (set! js/window.location
                                                    (str "/admin/content-types/"
                                                         (name (:slug content-type))
                                                         "/"
                                                         (:content-item-slug res)))))
                                          #_(set-state (assoc state :submitting false)))
                            http-opts' (-> http-opts
                                           (assoc :body body)
                                           (assoc :on-complete on-complete))]
                        (http/post submit-form-url http-opts')))
        router nil #_(permalinks/->permalink-router {:single permalink-single})]
    [:div {:class "p-4 pt-0"}
     (admin-impl/box
       {:title title
        :content
        (let [content-item-slug (or (get-in content-item [:fields "Slug"])
                                    (some-> (get-in content-item [:form-fields title-field-id]) ->slug))
              path-params {:content-type-slug (:slug content-type)
                           :content-item-slug content-item-slug}
              match nil #_(reitit/match-by-name router :single path-params)
              permalink-url (when-not (str/blank? content-item-slug)
                              (str site-base-url #_(reitit/match->path match)))
              fields (->> (:fields content-type)
                          (sort-by :rank)
                          (remove (comp #{"Slug"} :name))
                          (map-indexed vector))]
          [:div
           (for [message (distinct messages)]
             message)
           [:form {:on-submit #(submit-form % {:content-item-slug content-item-slug})}
            [:div
             (for [[i field] fields
                   :let [{:keys [type name]} field]]
               [:div {:key (:id field)}
                [:div
                 {:class (str "mb-2 pb-2 pt-2 "
                              (when-not (= i (dec (count fields)))
                                "border-b"))}
                 (case type
                   :text [html/input {:type :text
                                      :class (when (= (:name field) "Title")
                                               "w-full")
                                      :name name
                                      :placeholder name
                                      :default-value (get-in content-item [:form-fields (:id field)])
                                      :on-change #(update-field % (:id field))}]
                   :text-lg [editor {:class "h-72"
                                     :html (get-in content-item [:form-fields (:id field)])
                                     :on-start #(add-editor (:id field) %)}]
                   :choice [html/select]
                   :datetime [html/input {:type :text
                                          :name name
                                          :placeholder name}]
                   :number [html/input {:type :number
                                        :name name
                                        :placeholder name}])
                 (when (= (:name field) "Title")
                   [:div {:class "mt-2 text-sm"}
                    [:span {:class "text-gray-500"}
                     "Permalink: "]
                    (if permalink-url
                      [:a {:class "underline" :href permalink-url}
                       permalink-url]
                      [:span {:class "text-gray-400"} site-base-url "/…"])])]])
             [:button
              {:type :submit
               :class (str "text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
                           submit-button-class)
               :disabled submitting}
              (if submitting
                [:span [html/spinner] submitting-button-text]
                submit-button-text)]]]])})]))

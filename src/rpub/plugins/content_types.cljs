(ns rpub.plugins.content-types
  (:refer-clojure :exclude [random-uuid])
  (:require ["react" :refer [useEffect useState useCallback]]
            [clojure.string :as str]
            [rads.inflections :as inflections]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.lib.reagent :as r]))

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

(defn content-type-fields-form [{:keys [state set-state anti-forgery-token content-type class]}]
  (let [http-opts {:anti-forgery-token anti-forgery-token}
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
                               (set-state (update-in state [:content-type-index (:id content-type) :fields]
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
                           (set-state (update-in state [:content-type-index (:id content-type) :fields]
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
         [:label {:for :field-name}]
         [html/input
          {:type :text
           :size :text-medium
           :class (str "px-2 py-1 font-semibold border border-gray-300 "
                       "rounded-[6px] mr-4 max-w-xl")
           :placeholder "Field Name"
           :name :field-name
           #_#_:on-change #(update-field-name % content-type field)
           :readonly true
           :default-value (:name field)}]
         [:label {:for :field-type}]
         [html/select
          {:name :field-type
           :default-value (:type field)
           :readonly true
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

(defn all-content-types-page [{:keys [content-types anti-forgery-token]}]
  (let [content-types (map #(update % :created-at js/Date.parse) content-types)
        [state set-state] (useState {:content-type-index (index-by :id content-types)})
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
                                      (set-state (assoc-in state [:content-type-index (:id content-type) :name] value))))
                                  html/default-debounce-timeout-ms))
        new-content-type (fn [e]
                           (let [content-type (->content-type
                                                {:name "New Content Type"
                                                 :slug "new-content-type"
                                                 :fields []})]
                             (.preventDefault e)
                             (http/post "/api/new-content-type" http-opts)
                             (set-state (assoc-in state [:content-type-index (:id content-type)] content-type))))
        delete-content-type (fn [e content-type]
                              (when (js/confirm (str "Are you sure you want to delete \""
                                                     (:name content-type)
                                                     "\"?"))
                                (let [http-opts' (assoc http-opts
                                                        :body
                                                        {:content-type-id (:id content-type)})]
                                  (.preventDefault e)
                                  (http/post "/api/delete-content-type" http-opts')
                                  (set-state (update state :content-type-index #(dissoc % (:id content-type)))))))
        new-field (fn [e content-type field]
                    (.preventDefault e)
                    (let [rank (inc (apply max 0 (map :rank (:fields content-type))))
                          field' (-> (->field field) (assoc :rank rank))
                          http-opts' (assoc http-opts :body {:content-type-id (:id content-type)})]
                      (http/post "/api/new-content-type-field" http-opts')
                      (set-state (update-in state [:content-type-index (:id content-type) :fields] conj field'))))
        {:keys [content-type-index]} state
        content-types (->> (vals content-type-index) (sort-by :created-at >))]
    [:div
     [:div {:class "p-4"}
      [admin-impl/box
       {:title [:div {:class "flex items-center"}
                [:div "Content Types"]
                #_[html/button
                   {:class "font-app-sans ml-auto"
                    :on-click new-content-type}
                   "New Content Type"]]
        :content [admin-impl/content-item-counts {:content-types content-types}]}]]
     (for [content-type content-types]
       [:div {:class "p-4 pt-0" :key (:id content-type)}
        [admin-impl/box
         {:title [:div {:class "flex items-center group"}
                  [html/input {:type :text
                               :class "max-w-xl"
                               :size :text-2xl
                               :name :content-type-name
                               :placeholder "Name"
                               :readonly true
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
                       {:state state
                        :set-state set-state
                        :content-type content-type
                        :anti-forgery-token anti-forgery-token}]])}]])]))

(html/add-element :all-content-types-page (r/reactify-component all-content-types-page))

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
        [state set-state] (useState {:content-items (map (fn [content-item]
                                                           (update content-item :fields #(update-keys % name)))
                                                         content-items)})
        delete-row (fn [_ content-item]
                     (let [body {:content-item-id (:id content-item)}
                           on-complete (fn [_ err]
                                         (if err
                                           (println err)
                                           (set-state (update state :content-items
                                                              (fn [content-items]
                                                                (remove #(= (:id %) (:id content-item))
                                                                        content-items))))))
                           http-opts' (merge http-opts {:body body
                                                        :on-complete on-complete})]
                       (http/post "/api/delete-content-item" http-opts')))
        {:keys [content-items]} state]
    [:div {:class "p-4"}
     [admin-impl/table
      {:title (:name content-type)
       :columns columns
       :rows (map #(assoc % :content-type content-type) content-items)
       :delete-row delete-row}]]))

(html/add-element :single-content-type-page (r/reactify-component single-content-type-page))

(defn editor-impl [{:keys [on-start] :as props}]
  (let [id (str (gensym))
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

(defn spinner [_]
  [:svg {:class "inline w-4 h-4 me-3 text-white animate-spin" :aria-hidden "true" :role "status" :viewBox "0 0 100 101" :fill "none" :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" :fill "#E5E7EB"}]
   [:path {:d "M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" :fill "currentColor"}]])

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
        [state set-state] (useState {:submitting false
                                     :editors {}
                                     :content-item {:form-fields (or (:document content-item) {})}
                                     :messages []})
        add-editor #(set-state (assoc-in state [:editors %1] %2))
        add-message #(set-state (update state :messages conj %))
        update-field (fn [e field-id]
                       (let [value (-> e .-target .-value)]
                         (set-state (assoc-in state [:content-item :form-fields field-id] value))))
        submit-form (fn [e {:keys [content-item-slug]}]
                      (.preventDefault e)
                      (let [v (set-state (assoc state :submitting true))
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
                                          (set-state (assoc state :submitting false)))
                            http-opts' (-> http-opts
                                           (assoc :body body)
                                           (assoc :on-complete on-complete))]
                        (http/post submit-form-url http-opts')))
        router nil #_(permalinks/->permalink-router {:single permalink-single})]
    [:div {:class "p-4 pt-0"}
     (admin-impl/box
       {:title title
        :content
        (let [{:keys [content-item messages submitting]} state
              content-item-slug (or (get-in content-item [:fields "Slug"])
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
                [:span [spinner] submitting-button-text]
                submit-button-text)]]]])})]))

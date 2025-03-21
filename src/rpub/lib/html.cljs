(ns rpub.lib.html
  {:no-doc true}
  (:require ["react" :refer [useState useRef useEffect] :as react]
            [clojure.set :as set]
            [clojure.string :as str]
            [rpub.lib.transit :as transit]))

(def default-debounce-timeout-ms 500)

(defn debounce [f wait]
  (let [timeout (atom nil)]
    (fn [& args]
      (when-let [v @timeout] (js/clearTimeout v))
      (reset! timeout (js/setTimeout (fn [] (apply f args)) wait)))))

(when-not (.-React js/globalThis)
  (set! (.-React js/globalThis) react))

(defn c [s]
  (str/replace s "." " "))

(defn- json-attrs->map [el]
  (into {}
        (for [attr (.-attributes el)]
          [(keyword (.-name attr))
           (some-> attr .-value js/JSON.parse (js->clj :keywordize-keys true))])))

(defn- transit-attrs->map [el]
  (into {}
        (for [attr (.-attributes el)]
          [(keyword (.-name attr))
           (some-> attr .-value transit/read)])))

(defn add-element [tag component & {:as opts}]
  (let [opts' (merge {:format :json} opts)
        Component #(component (.-children %))]
    (when-not (.get js/customElements (name tag))
      (let [klass (fn self [] (js/Reflect.construct js/HTMLElement #js[] self))]
        (set! (.-prototype klass) (js/Object.create js/HTMLElement.prototype))
        (set! (.-render (.-prototype klass))
              (fn [props]
                (this-as
                  this
                  (react/render #jsx [Component props] this))))
        (set! (.-connectedCallback (.-prototype klass))
              (fn []
                (this-as
                  this
                  (when-not (zero? (-> this .-attributes .-length))
                    (let [props (case (:format opts')
                                  :json (json-attrs->map this)
                                  :transit (transit-attrs->map this))]
                      (.render this props))))))
        (js/customElements.define (name tag) klass)))))

(defn custom-element [[tag props]]
  (let [f (fn [_]
            (let [ref (useRef nil)]
              (useEffect
                (fn []
                  (when (.-current ref)
                    (.render (.-current ref) props)))
                #js[props])
              (React/createElement (name tag) #js{:ref ref})))]
    [f props]))

(defn button [props]
  (let [defaults {:color :blue}
        classes {:class (c "flex.items-center.justify-center.text-white.focus:ring-4.font-medium.rounded-lg.text-sm.px-4.py-2.focus:outline-none.shadow.transition-colors.duration-75")}
        attrs' (as-> defaults $
                 (merge $ props)
                 (assoc $ :class (str (:class $) " "
                                      "bg-" (name (:color $)) "-700 "
                                      "hover:bg-" (name (:color $)) "-800 "
                                      "focus:ring-" (name (:color $)) "-300"))
                 (merge-with #(str %1 " " %2) $ classes)
                 (select-keys $ [:class :on-click])
                 (set/rename-keys $ {:on-click :onClick}))]
    [:button attrs' (:children props)]))

(defn action-button [props]
  (let [classes {:class (c "flex.items-center.justify-center.text-white.focus:ring-4.font-medium.rounded-lg.text-sm.px-4.py-2.focus:outline-none.shadow.transition-colors.duration-75")}
        attrs' (as-> {} $
                 (merge $ props)
                 (assoc $ :class (str (:class $) " "
                                      "bg-blue-700 "
                                      "hover:bg-blue-800 "
                                      "focus:ring-blue-300"))
                 (merge-with #(str %1 " " %2) $ classes)
                 (select-keys $ [:class :on-click])
                 (set/rename-keys $ {:on-click :onClick}))]
    [:button attrs' (:children props)]))

(defn delete-button [props]
  (let [classes {:class (c "font-app-sans.flex.items-center.justify-center.text-white.focus:ring-4.font-medium.rounded-lg.text-sm.px-4.py-2.focus:outline-none.shadow.transition-colors.duration-75")}
        attrs' (as-> {} $
                 (merge $ props)
                 (assoc $ :class (str (:class $) " "
                                      "bg-red-700 "
                                      "hover:bg-red-800 "
                                      "focus:ring-red-300"))
                 (merge-with #(str %1 " " %2) $ classes)
                 (select-keys $ [:class :on-click])
                 (set/rename-keys $ {:on-click :onClick}))]
    [:button attrs' (:children props)]))

(defn activated-button [{:keys [on-click]}]
  (let [[hover set-hover] (useState false)]
    [:div {:class "ml-auto"
           :onMouseEnter #(set-hover true)
           :onMouseLeave #(set-hover false)
           :onClick on-click}
     (if hover
       [:button {:type "submit" :class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-red-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow w-44"}
        [:div {:class "inline-flex items-center mx-auto"}
         [:svg {:class "w-6 h-6 text-white dark:text-white mr-2" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
          [:path {:fill-rule "evenodd" :d "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm5.757-1a1 1 0 1 0 0 2h8.486a1 1 0 1 0 0-2H7.757Z" :clip-rule "evenodd"}]]
         "Deactivate"]]
       [:button {:type "submit" :class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-green-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow-inner w-44"}
        [:div {:class "inline-flex items-center mx-auto"}
         [:svg {:class "w-6 h-6 text-white mr-2" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
          [:path {:fill-rule "evenodd" :d "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z" :clip-rule "evenodd"}]]
         "Active"]])]))

(defn activate-button [{:keys [on-click label]}]
  [:button {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44"
            :onClick on-click}
   [:div {:class "inline-flex items-center mx-auto"}
    [:svg {:class "w-6 h-6 text-white dark:text-white mr-2" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
     [:path {:fill-rule "evenodd" :d "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm11-4.243a1 1 0 1 0-2 0V11H7.757a1 1 0 1 0 0 2H11v3.243a1 1 0 1 0 2 0V13h3.243a1 1 0 1 0 0-2H13V7.757Z" :clip-rule "evenodd"}]]
    label]])

(defn input [props]
  (let [{:keys [type class size default-value placeholder on-change on-focus on-blur readonly]
         input-name :name} props
        [current-value set-current-value] (useState default-value)]
    [:input
     (merge {:type (name type)
             :class (str "bg-gray-50 border border-gray-200 text-gray-900 rounded-lg
                   focus:border-primary-600 focus:ring-0 focus:ring-offset-0 block w-full
                   p-2 5 dark:bg-gray-700 dark:border-gray-600
                   dark:placeholder-gray-400 dark:text-white
                   focus:ring-2
                   dark:focus:ring-primary-500 dark:focus:border-primary-500 "
                         (name (or size :text-sm)) " "
                         class)
             :name (name input-name)
             :placeholder placeholder
             :onFocus on-focus
             :onBlur on-blur
             :onChange (fn [e]
                         (let [value (-> e .-target .-value)]
                           (set-current-value value)
                           (on-change e)))
             :value current-value}
            (when readonly {:readonly readonly}))]))

(defn input2 [props]
  (let [{:keys [key type class size value placeholder on-change readonly]
         input-name :name} props]
    [:input
     (merge {:key key
             :type (name type)
             :class (str "bg-gray-50 border border-gray-300 text-gray-900 rounded-lg
                         focus:ring-primary-600 focus:border-primary-600 block w-full
                         p-2 5 dark:bg-gray-700 dark:border-gray-600
                         dark:placeholder-gray-400 dark:text-white
                         focus:ring-0 focus:ring-offset-0
                         dark:focus:ring-primary-500 dark:focus:border-primary-500 "
                         (name (or size :text-sm)) " "
                         class)
             :name (name input-name)
             :placeholder placeholder
             :onChange on-change
             :value value}
            (when readonly {:readonly readonly}))]))

(defn select [{:keys [type default-value placeholder on-focus on-blur children]
               input-name :name}]
  (let [[current-value] (useState default-value)]
    (apply js/React.createElement
           "select"
           (clj->js {:type type
                     :name input-name
                     :class (str "appearance-none px-2 py-1 border border-gray-200 "
                                 "focus:ring-2 rounded-[6px] mr-4")
                     :placeholder placeholder
                     :onFocus on-focus
                     :onBlur on-blur
                     #_#_:on-change (fn [e]
                                      (let [value (-> e .-target .-value)]
                                        (set-current-value value)
                                        (on-change e)))
                     :value current-value})
           children)))

(defn textarea [props]
  (let [{:keys [key class size value placeholder on-change readonly]
         input-name :name} props]
    [:textarea
     (merge {:key key
             :class (str "bg-gray-50 border border-gray-300 text-gray-900 rounded-lg
                         focus:ring-primary-600 focus:border-primary-600 block w-full
                         p-2 5 dark:bg-gray-700 dark:border-gray-600
                         dark:placeholder-gray-400 dark:text-white
                         focus:ring-0 focus:ring-offset-0
                         dark:focus:ring-primary-500 dark:focus:border-primary-500 "
                         (name (or size :text-sm)) " "
                         class)
             :name (name input-name)
             :placeholder placeholder
             :onChange on-change}
            (when readonly {:readonly readonly}))
     value]))

(defn spinner [_]
  [:svg {:class "inline w-4 h-4 me-3 text-white animate-spin" :aria-hidden "true" :role "status" :viewBox "0 0 100 101" :fill "none" :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" :fill "#E5E7EB"}]
   [:path {:d "M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" :fill "currentColor"}]])

(defn submit-button [{:keys [ready-label submit-label submitting]}]
  [:button
   {:type :submit
    :class "w-[120px] text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
    :disabled submitting}
   (if submitting
     [:span [spinner] submit-label]
     ready-label)])

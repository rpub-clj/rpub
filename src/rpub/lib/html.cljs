(ns rpub.lib.html
  {:no-doc true}
  (:require ["react" :refer [useState] :as react]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.walk :as walk]))

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

(defn- attrs->map [el]
  (into {}
        (for [attr (.-attributes el)]
          [(keyword (.-name attr)) (some-> attr .-value JSON/parse (js->clj :keywordize-keys true))])))

(defn add-element [tag component]
  (when-not (.get js/customElements (name tag))
    (let [klass (fn self [] (js/Reflect.construct js/HTMLElement #js[] self))]
      (set! (.-prototype klass) (js/Object.create js/HTMLElement.prototype))
      (set! (.-connectedCallback (.-prototype klass))
            (fn []
              (this-as
                this
                (let [render (fn []
                               (let [props (attrs->map this)
                                     Component #(component (.-children %))]
                                 (react/render #jsx [Component props] this)))]
                  (render)))))
      (js/customElements.define (name tag) klass))))

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

(defn input [props]
  (let [props' (js->clj props :keywordize-keys true)
        {:keys [type class size default-value placeholder on-change readonly]
         input-name :name} props'
        [current-value set-current-value] (useState default-value)]
    [:input
     (merge {:type (name type)
             :class (str "bg-gray-50 border border-gray-300 text-gray-900 rounded-lg
                   focus:ring-primary-600 focus:border-primary-600 block w-full
                   p-2 5 dark:bg-gray-700 dark:border-gray-600
                   dark:placeholder-gray-400 dark:text-white
                   dark:focus:ring-primary-500 dark:focus:border-primary-500 "
                         (name (or size :text-sm)) " "
                         class)
             :name (name input-name)
             :placeholder placeholder
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
                         dark:focus:ring-primary-500 dark:focus:border-primary-500 "
                         (name (or size :text-sm)) " "
                         class)
             :name (name input-name)
             :placeholder placeholder
             :onChange on-change
             :value value}
            (when readonly {:readonly readonly}))]))

(defn select [{:keys [type default-value placeholder on-change children]
               input-name :name}]
  (let [[current-value set-current-value] (useState default-value)]
    (apply js/React.createElement
           "select"
           (clj->js {:type type
                     :name input-name
                     :class (str "appearance-none px-2 py-1 border border-gray-300 "
                                 "rounded-[6px] mr-4")
                     :placeholder placeholder
                     :disabled true
                     #_#_:on-change (fn [e]
                                      (let [value (-> e .-target .-value)]
                                        (set-current-value value)
                                        (on-change e)))
                     :value current-value})
           children)))

(defn spinner [_]
  [:svg {:class "inline w-4 h-4 me-3 text-white animate-spin" :aria-hidden "true" :role "status" :viewBox "0 0 100 101" :fill "none" :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" :fill "#E5E7EB"}]
   [:path {:d "M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" :fill "currentColor"}]])

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

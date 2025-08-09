(ns rpub.plugins.admin.plugins.page
  (:require [clojure.string :as str]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.lib.substrate :refer [subscribe dispatch]]
            [rpub.plugins.admin.helpers :as helpers]))

(defn- plugin-icon [_]
  [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
   [:path {:fill-rule "evenodd" :d "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z" :clip-rule "evenodd"}]
   [:path {:fill-rule "evenodd" :d "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]])

(defn- plugin-url [plugin]
  (let [[plugin-ns] (str/split (:key plugin) #"/")
        suffix (last (str/split plugin-ns #"\."))]
    (str "https://github.com/rpub-clj/plugins/tree/main/plugins/" suffix)))

(defn- page [{:keys [current-plugins available-plugins] :as _props}]
  (let [needs-restart (subscribe [::needs-restart])
        activated-plugins (subscribe [::activated-plugins])
        current-plugin-index (helpers/index-by :key current-plugins)
        activate-plugin (fn [_e plugin]
                          (let [plugin' (assoc plugin :activated true)
                                body {:plugin (select-keys plugin' [:key])}]
                            (-> (http/post "/admin/api/activate-plugin" {:body body})
                                (.then (fn [_] (dispatch [::activate-plugin (:key plugin')]))))))
        deactivate-plugin (fn [_e plugin]
                            (let [body {:plugin (select-keys plugin [:key])}]
                              (-> (http/post "/admin/api/deactivate-plugin" {:body body})
                                  (.then (fn [_] (dispatch [::deactivate-plugin (:key plugin)]))))))
        restart-server (fn [_e]
                         (dispatch [::restart-server])
                         (http/post "/api/restart-server" {}))
        available-plugin-index (helpers/index-by :key available-plugins)
        activated-plugin-index (->> activated-plugins
                                    (map (fn [k] [k {:activated true}]))
                                    (into {}))
        combined-plugin-index (merge-with merge
                                          current-plugin-index
                                          available-plugin-index
                                          activated-plugin-index)]
    [:div {:class "p-4"}
     [helpers/box
      {:title "Plugins"
       :class "mb-4"
       :content
       [:div {:class "flex"}
        [:p {:class "italic"} "Note: The server must be restarted after activating a plugin for the first time."]
        (when needs-restart
          [:button {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 hover:bg-primary-800 shadow-sm ml-auto w-44"
                    :on-click restart-server}
           [:div {:class "inline-flex items-center mx-auto"}
            "Restart"]])]}]
     (for [plugin (sort-by #(str/lower-case (or (:label %) (:key %)))
                           (doto (vals combined-plugin-index)
                             prn))]
       [helpers/box
        {:key (:key plugin)
         :title [:div {:class "flex items-center" :style {:margin-top "-1px"}}
                 [plugin-icon]
                 [:a {:class "underline" :href (plugin-url plugin) :target "_blank"}
                  (or (:label plugin) (:key plugin))]
                 (if (:activated plugin)
                   [html/activated-button {:on-click #(deactivate-plugin % plugin)}]
                   [html/activate-button {:label "Activate"
                                          :on-click #(activate-plugin % plugin)}])]
         :class "mb-4"
         :content
         [:div
          (when-let [v (:description plugin)]
            [:p v])]}])]))

(defn activate-plugin [db [_ k]]
  (-> db
      (assoc ::needs-restart true)
      (update ::activated-plugins (fnil conj #{}) k)))

(defn deactivate-plugin [db [_ k]]
  (-> db
      (assoc ::needs-restart true)
      (update ::activated-plugins disj k)))

(defn restart-server [db _]
  (assoc db ::restarted true))

(def model
  {:queries
   {::needs-restart (fn [db _] (::needs-restart db))
    ::activated-plugins (fn [db _] (::activated-plugins db))}

   :transactions
   {::activate-plugin activate-plugin
    ::deactivate-plugin deactivate-plugin
    ::restart-server restart-server}})

(def config
  {:page-id :plugins-page
   :model model
   :component page})

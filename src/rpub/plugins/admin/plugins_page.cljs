(ns rpub.plugins.admin.plugins-page
  (:require [clojure.string :as str]
            [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.impl :as admin-impl]))

(defn- plugin-icon [_]
  [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
   [:path {:fill-rule "evenodd" :d "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z" :clip-rule "evenodd"}]
   [:path {:fill-rule "evenodd" :d "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]])

(defn- plugin-url [plugin]
  (let [[plugin-ns] (str/split (:key plugin) #"/")
        suffix (last (str/split plugin-ns #"\."))]
    (str "https://github.com/rpub-clj/plugins/tree/main/plugins/" suffix)))

(defn- page [{:keys [current-plugins available-plugins] :as _props}]
  (let [[{:keys [::needs-restart
                 ::activated-plugins]}
         push] (use-dag [::needs-restart
                         ::activated-plugins])
        http-opts {:format :transit}
        current-plugin-index (admin-impl/index-by :key current-plugins)
        activate-plugin (fn [_e plugin]
                          (let [plugin' (assoc plugin :activated true)
                                body {:plugin (select-keys plugin' [:key])}
                                on-complete (fn [res _err]
                                              (when res
                                                (push ::activate-plugin (:key plugin'))))
                                http-opts' (merge http-opts {:body body
                                                             :on-complete on-complete})]
                            (http/post "/admin/api/activate-plugin" http-opts')))
        deactivate-plugin (fn [_e plugin]
                            (let [body {:plugin (select-keys plugin [:key])}
                                  on-complete (fn [res _err]
                                                (when res
                                                  (push ::deactivate-plugin (:key plugin))))
                                  http-opts' (merge http-opts {:body body
                                                               :on-complete on-complete})]
                              (http/post "/admin/api/deactivate-plugin" http-opts')))
        restart-server (fn [_e]
                         (let [on-complete (fn [_res _err])
                               http-opts' (merge http-opts {:on-complete on-complete})]
                           (push ::restart-server)
                           (http/post "/api/restart-server" http-opts')))
        available-plugin-index (admin-impl/index-by :key available-plugins)
        activated-plugin-index (->> activated-plugins
                                    (map (fn [k] [k {:activated true}]))
                                    (into {}))
        combined-plugin-index (-> (merge-with merge
                                              current-plugin-index
                                              available-plugin-index
                                              activated-plugin-index))]
    [:div {:class "p-4"}
     [admin-impl/box
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
                           (vals combined-plugin-index))]
       [admin-impl/box
        {:key (:key plugin)
         :title [:div {:class "flex items-center" :style {:margin-top "-1px"}}
                 [plugin-icon]
                 [:a {:class "underline" :href (plugin-url plugin) :target "_blank"}
                  (or (:label plugin) (:key plugin))]
                 (if (:activated plugin)
                   [html/activated-button {:on-click #(deactivate-plugin % plugin)}]
                   [html/activate-button {:label "Activate Plugin"
                                          :on-click #(activate-plugin % plugin)}])]
         :class "mb-4"
         :content
         [:div
          (when-let [v (:description plugin)]
            [:p v])]}])]))

(defn activate-plugin [db k]
  (-> db
      (assoc ::needs-restart true)
      (update ::activated-plugins (fnil conj #{}) k)))

(defn deactivate-plugin [db k]
  (-> db
      (assoc ::needs-restart true)
      (update ::activated-plugins disj k)))

(defn restart-server [db]
  (assoc db ::restarted true))

(def dag-config
  {:nodes
   {::needs-restart {:calc ::needs-restart}
    ::restart-server {:push restart-server}
    ::activated-plugins {:calc ::activated-plugins}
    ::activate-plugin {:push activate-plugin}
    ::deactivate-plugin {:push deactivate-plugin}}

   :edges
   [[::activate-plugin ::needs-restart]
    [::activate-plugin ::activated-plugins]
    [::deactivate-plugin ::needs-restart]
    [::deactivate-plugin ::activated-plugins]]})

(def config
  {:page-id :plugins-page
   :component page
   :dag-config dag-config})

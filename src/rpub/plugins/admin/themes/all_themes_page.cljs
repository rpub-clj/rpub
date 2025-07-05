(ns rpub.plugins.admin.themes.all-themes-page
  (:require ["react" :refer [useEffect]]
            [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.admin.helpers :as helpers]
            [rpub.plugins.admin.roles :as roles]))

(defn edit-icon [_]
  [:svg {:className "w-6 h-6 text-white" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "none" :viewBox "0 0 24 24"}
   [:path {:stroke "currentColor" :strokeLinecap "round" :strokeLinejoin "round" :strokeWidth "2" :d "m14.304 4.844 2.852 2.852M7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-4.5m2.409-9.91a2.017 2.017 0 0 1 0 2.853l-6.844 6.844L8 14l.713-3.565 6.844-6.844a2.015 2.015 0 0 1 2.852 0Z"}]])

(defn- theme-icon [_]
  [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
   [:path {:fill-rule "evenodd" :d "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z" :clip-rule "evenodd"}]
   [:path {:fill-rule "evenodd" :d "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z" :clip-rule "evenodd"}]])

(defn activate-theme! [e theme push]
  (.preventDefault e)
  (let [body {:settings [{:key :theme-name, :value (:label theme)}]}]
    (-> (http/post "/admin/api/update-settings" {:body body})
        (.then (fn [_] (push ::activate-theme (:label theme)))))))

(defn- page [{:keys [theme-name-setting current-user] :as props}]
  (let [[{:keys [::current-theme-name-setting ::themes]}
         push] (use-dag [::current-theme-name-setting ::themes])
        _ (useEffect (fn [] (push ::init props)) #js[])
        theme-name-value (:value (or current-theme-name-setting
                                     theme-name-setting))
        activated? #(= (:label %) theme-name-value)]
    [:div {:class "p-4"}
     [helpers/box
      {:title
       [:div {:class "flex items-center"}
        [:div.grow "Themes"]
        (when (roles/allowed? current-user {:resource :themes, :action :create})
          [:a {:href "/admin/themes/new"}
           [html/button {:class "font-app-sans ml-auto"}
            "New Theme"]])]
       :class "mb-4"}]
     (for [theme themes
           :let [activated (activated? theme)]]
       [:form {:method "post"}
        [helpers/box
         {:key (:label theme)
          :title [:div {:class "flex items-center" :style {:margin-top "-1px"}}
                  [:div {:class "grow flex items-center"}
                   [theme-icon]
                   (:label theme)]
                  [:div
                   (when (:editable theme)
                     [:a {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 hover:bg-primary-800 shadow-sm mr-4 w-44"
                          :href (str "/admin/themes/" (:id theme))}
                      [:div {:class "inline-flex items-center mx-auto"}
                       [edit-icon]
                       "Edit"]])
                   (if activated
                     [:button {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-emerald-600 rounded-lg focus:ring-4 focus:ring-primary-200 hover:bg-primary-800 shadow-inner cursor-auto w-44"
                               :type "submit"}
                      [:div {:class "inline-flex items-center mx-auto"}
                       [:svg {:class "w-6 h-6 text-white mr-2" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
                        [:path {:fill-rule "evenodd" :d "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z" :clip-rule "evenodd"}]]
                       "Active"]]
                     [:button {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 hover:bg-primary-800 shadow-sm w-44"
                               :on-click #(activate-theme! % theme push)
                               :type "submit"}
                      [:div {:class "inline-flex items-center mx-auto"}
                       [:svg {:class "w-6 h-6 text-white mr-2" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
                        [:path {:d "M8 5v4.997a.31.31 0 0 1-.068.113c-.08.098-.213.207-.378.301-.947.543-1.713 1.54-2.191 2.488A6.237 6.237 0 0 0 4.82 14.4c-.1.48-.138 1.031.018 1.539C5.12 16.846 6.02 17 6.414 17H11v3a1 1 0 1 0 2 0v-3h4.586c.395 0 1.295-.154 1.575-1.061.156-.508.118-1.059.017-1.539a6.241 6.241 0 0 0-.541-1.5c-.479-.95-1.244-1.946-2.191-2.489a1.393 1.393 0 0 1-.378-.301.309.309 0 0 1-.068-.113V5h1a1 1 0 1 0 0-2H7a1 1 0 1 0 0 2h1Z"}]]
                       "Activate"]])]]
          :class "mb-4"
          :content
          (when-let [v (:description theme)]
            [:p v])}]])]))

(defn init [db {:keys [themes]}]
  (merge db {::themes themes}))

(defn activate-theme [db theme-label]
  (assoc db ::current-theme-name-setting {:value theme-label}))

(def dag-config
  {:nodes
   {::init {:push init}
    ::activate-theme {:push activate-theme}
    ::current-theme-name-setting {:calc ::current-theme-name-setting}
    ::themes {:calc ::themes}}

   :edges
   [[::activate-theme ::current-theme-name-setting]
    [::init ::themes]]})

(def config
  {:page-id :all-themes-page
   :component page
   :dag-config dag-config})

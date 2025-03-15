(ns rpub.admin
  (:require ["flowbite"]
            [clojure.string :as str]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.dag.react :refer [use-dag]]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]
            [rpub.plugins.content-types]))

(defn- dashboard-content-types [{:keys [content-types]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-content-types"}
   [admin-impl/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M5.005 10.19a1 1 0 0 1 1 1v.233l5.998 3.464L18 11.423v-.232a1 1 0 1 1 2 0V12a1 1 0 0 1-.5.866l-6.997 4.042a1 1 0 0 1-1 0l-6.998-4.042a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1ZM5 15.15a1 1 0 0 1 1 1v.232l5.997 3.464 5.998-3.464v-.232a1 1 0 1 1 2 0v.81a1 1 0 0 1-.5.865l-6.998 4.042a1 1 0 0 1-1 0L4.5 17.824a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1Z" :clip-rule "evenodd"}]
       [:path {:d "M12.503 2.134a1 1 0 0 0-1 0L4.501 6.17A1 1 0 0 0 4.5 7.902l7.002 4.047a1 1 0 0 0 1 0l6.998-4.04a1 1 0 0 0 0-1.732l-6.997-4.042Z"}]]
      "Content Types"]
     :class "md:h-48"
     :size :half
     :content
     [:div
      (let [[num word] (admin-impl/pluralize content-types "types")]
        [:p {:class "mb-4"} "This site has " [:span {:class "font-semibold"} num] " " word " of content:"])
      [admin-impl/content-item-counts {:content-types content-types}]]}]])

(defn- dashboard-theme [{:keys [theme]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-theme"}
   [admin-impl/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z" :clip-rule "evenodd"}]]
      "Theme"]
     :class "md:h-48"
     :size :half
     :content
     [:div "This site is using the " [:span {:class "font-semibold underline"} (:label theme)] "."]}]])

(defn- dashboard-plugins [{:keys [activated-plugins]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-plugins"}
   [admin-impl/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]]
      "Plugins"]
     :class "md:h-48"
     :size :half
     :content
     [:div
      (let [[num word] (admin-impl/pluralize activated-plugins "plugin")]
        [:p {:class "mb-4"} "This site has " [:span {:class "font-semibold"} num] " " word " activated:"])
      [:div
       (for [[i plugin] (map-indexed vector (sort-by :label activated-plugins))]
         [:span
          (when-not (= i 0)
            [:span {:class "text-gray-300"} " • "])
          [:a {:class "underline text-nowrap font-semibold"
               :href "/admin/plugins"}
           (:label plugin)]])]]}]])

(defn- dashboard-settings [{:keys [settings]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-settings"}
   [admin-impl/box
    {:title [:div {:class "flex items-center"}
             [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
              [:path {:fill-rule "evenodd" :d "M9.586 2.586A2 2 0 0 1 11 2h2a2 2 0 0 1 2 2v.089l.473.196.063-.063a2.002 2.002 0 0 1 2.828 0l1.414 1.414a2 2 0 0 1 0 2.827l-.063.064.196.473H20a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-.089l-.196.473.063.063a2.002 2.002 0 0 1 0 2.828l-1.414 1.414a2 2 0 0 1-2.828 0l-.063-.063-.473.196V20a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2v-.089l-.473-.196-.063.063a2.002 2.002 0 0 1-2.828 0l-1.414-1.414a2 2 0 0 1 0-2.827l.063-.064L4.089 15H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h.09l.195-.473-.063-.063a2 2 0 0 1 0-2.828l1.414-1.414a2 2 0 0 1 2.827 0l.064.063L9 4.089V4a2 2 0 0 1 .586-1.414ZM8 12a4 4 0 1 1 8 0 4 4 0 0 1-8 0Z" :clip-rule "evenodd"}]]
             "Settings"]
     :class "md:h-48"
     :size :half
     :content
     [:div
      [:div
       [:span {:class "font-semibold"} "Permalinks: "]
       [:code (get-in settings [:permalink-single :value])]]]}]])

(defn- dashboard-user [{:keys [current-user]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-user"}
   [admin-impl/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M12 20a7.966 7.966 0 0 1-5.002-1.756l.002.001v-.683c0-1.794 1.492-3.25 3.333-3.25h3.334c1.84 0 3.333 1.456 3.333 3.25v.683A7.966 7.966 0 0 1 12 20ZM2 12C2 6.477 6.477 2 12 2s10 4.477 10 10c0 5.5-4.44 9.963-9.932 10h-.138C6.438 21.962 2 17.5 2 12Zm10-5c-1.84 0-3.333 1.455-3.333 3.25S10.159 13.5 12 13.5c1.84 0 3.333-1.455 3.333-3.25S13.841 7 12 7Z" :clip-rule "evenodd"}]]
      "User"]
     :class "md:h-48"
     :size :half
     :content
     [:div
      "You're logged in as "
      [:a {:class "font-semibold underline", :href "/admin/users"}
       (:username current-user)]
      "."]}]])

(defn- dashboard-server [{:keys [rpub-version]}]
  (let [rpub-url (str "https://github.com/rpub-clj/rpub/tree/v" rpub-version)]
    [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
           :data-test-id "dashboard-server"}
     [admin-impl/box
      {:title
       [:div {:class "flex items-center"}
        [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
         [:path {:fill-rule "evenodd" :d "M5 5a2 2 0 0 0-2 2v3a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1V7a2 2 0 0 0-2-2H5Zm9 2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17ZM3 17v-3a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v3a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2Zm11-2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]]
        "Server"]
       :class "md:h-48"
       :size :half
       :content
       [:div
        "This server is running "
        [:a {:class "font-semibold underline"
             :href rpub-url
             :target "_blank"}
         (str "rPub v" rpub-version)]
        "."]}]]))

(defn- dashboard-page [props]
  [:div {:class "flex flex-wrap py-4 px-4 md:px-2"}
   [dashboard-content-types props]
   [dashboard-theme props]
   [dashboard-plugins props]
   [dashboard-settings props]
   [dashboard-user props]
   [dashboard-server props]])

(html/add-element :dashboard-page
                  (admin-impl/wrap-component dashboard-page)
                  {:format :transit})

(defn- settings-page [{:keys [anti-forgery-token settings] :as _props}]
  (let [[{:keys [:settings-page/field-values
                 :settings-page/submitting]}
         push] (use-dag [:settings-page/field-values
                         :settings-page/submitting])
        settings-index (admin-impl/index-by :key settings)
        http-opts {:anti-forgery-token anti-forgery-token
                   :format :transit}
        update-setting (fn [setting-key e]
                         (let [value (-> e .-target .-value)]
                           (push :settings-page/change-input [setting-key value])))
        submit-form (fn [e]
                      (.preventDefault e)
                      (push :settings-page/submit-start)
                      (let [on-complete (fn [_ err]
                                          (if err
                                            (push :settings-page/submit-error)
                                            (.reload (.-location js/window))))
                            settings (-> (merge-with #(assoc %1 :value %2)
                                                     settings-index
                                                     field-values)
                                         (update-vals #(select-keys % [:key :value]))
                                         vals)
                            http-opts' (merge http-opts {:body {:settings settings}
                                                         :on-complete on-complete})]
                        (http/post "/api/update-settings" http-opts')))]
    [:div {:class "p-4"}
     [admin-impl/box
      {:title "Settings"
       :content
       [:section {:class "bg-white dark:bg-gray-900"}
        [:div {:class "max-w-2xl"}
         [:form {:onSubmit submit-form}
          [:div {:class "grid gap-4 sm:grid-cols-2 sm:gap-6"}
           (for [setting (sort-by :label (vals settings-index))]
             [:div {:key (:key setting) :class "sm:col-span-2"}
              [:label {:class "block mb-2 text-sm font-semibold text-gray-900 dark:text-white" :for "name"}
               (:label setting)]
              [html/input2
               {:type :text
                :name (:key setting)
                :value (or (get field-values (:key setting)) (:value setting))
                :on-change #(update-setting (:key setting) %)}]])
           [:button
            {:type :submit
             :class "w-[120px] text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
             :disabled submitting}
            (if submitting
              [:span [html/spinner] "Saving"]
              "Save")]]]]]}]]))

(html/add-element :settings-page
                  (admin-impl/wrap-component settings-page)
                  {:format :transit})

(def ^:private columns
  [{:name "Username"
    :value (fn [{:keys [username]}]
             [:a {:class "font-semibold"} username])}])

(defn- users-page [{:keys [users]}]
  [:div {:class "p-4"}
   [admin-impl/table
    {:title "Users"
     :columns columns
     :rows users}]])

(html/add-element :users-page
                  (admin-impl/wrap-component users-page)
                  {:format :transit})

(defn- theme-icon [_]
  [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
   [:path {:fill-rule "evenodd" :d "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z" :clip-rule "evenodd"}]
   [:path {:fill-rule "evenodd" :d "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z" :clip-rule "evenodd"}]])

(defn- themes-page [{:keys [themes theme-name-setting anti-forgery-token]}]
  (let [[{:keys [:themes-page/current-theme-name-setting]}
         push] (use-dag [:themes-page/current-theme-name-setting])
        http-opts {:anti-forgery-token anti-forgery-token
                   :format :transit}
        theme-name-value (:value (or current-theme-name-setting
                                     theme-name-setting))
        activated? #(= (:label %) theme-name-value)
        activate-theme (fn [theme e]
                         (.preventDefault e)
                         (let [body {:settings [{:key :theme-name
                                                 :value (:label theme)}]}
                               on-complete (fn [res _err]
                                             (when res
                                               (push :themes-page/activate-theme (:label theme))))
                               http-opts' (merge http-opts
                                                 {:body body
                                                  :on-complete on-complete})]
                           (http/post "/api/update-settings" http-opts')))]
    [:div {:class "p-4"}
     (for [theme themes
           :let [activated (activated? theme)]]
       [:form {:method "post"}
        [admin-impl/box
         {:key (:label theme)
          :title [:div {:class "flex items-center" :style {:margin-top "-1px"}}
                  [theme-icon]
                  (:label theme)
                  (if activated
                    [:button {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-green-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow-inner ml-auto cursor-auto w-44"
                              :type "submit"}
                     [:div {:class "inline-flex items-center mx-auto"}
                      [:svg {:class "w-6 h-6 text-white mr-2" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
                       [:path {:fill-rule "evenodd" :d "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z" :clip-rule "evenodd"}]]
                      "Active"]]
                    [:button {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44"
                              :onClick #(activate-theme theme %)
                              :type "submit"}
                     [:div {:class "inline-flex items-center mx-auto"}
                      [:svg {:class "w-6 h-6 text-white dark:text-white mr-2" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
                       [:path {:d "M8 5v4.997a.31.31 0 0 1-.068.113c-.08.098-.213.207-.378.301-.947.543-1.713 1.54-2.191 2.488A6.237 6.237 0 0 0 4.82 14.4c-.1.48-.138 1.031.018 1.539C5.12 16.846 6.02 17 6.414 17H11v3a1 1 0 1 0 2 0v-3h4.586c.395 0 1.295-.154 1.575-1.061.156-.508.118-1.059.017-1.539a6.241 6.241 0 0 0-.541-1.5c-.479-.95-1.244-1.946-2.191-2.489a1.393 1.393 0 0 1-.378-.301.309.309 0 0 1-.068-.113V5h1a1 1 0 1 0 0-2H7a1 1 0 1 0 0 2h1Z"}]]
                      "Activate Theme"]])]
          :class "mb-4"
          :content
          (when-let [v (:description theme)]
            [:p v])}]])]))

(html/add-element :themes-page
                  (admin-impl/wrap-component themes-page)
                  {:format :transit})

(defn- plugin-icon [_]
  [:svg {:class "w-8 h-8 text-gray-500 dark:text-white mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
   [:path {:fill-rule "evenodd" :d "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z" :clip-rule "evenodd"}]
   [:path {:fill-rule "evenodd" :d "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]])

(defn- plugin-url [plugin]
  (let [[plugin-ns] (str/split (:key plugin) #"/")
        suffix (last (str/split plugin-ns #"\."))]
    (str "https://github.com/rpub-clj/plugins/tree/main/plugins/" suffix)))

(defn- plugins-page [{:keys [current-plugins available-plugins anti-forgery-token] :as _props}]
  (let [[{:keys [:plugins-page/needs-restart
                 :plugins-page/activated-plugins]}
         push] (use-dag [:plugins-page/needs-restart
                         :plugins-page/activated-plugins])
        http-opts {:anti-forgery-token anti-forgery-token
                   :format :transit}
        current-plugin-index (admin-impl/index-by :key current-plugins)
        activate-plugin (fn [_e plugin]
                          (let [plugin' (assoc plugin :activated true)
                                body {:plugin (select-keys plugin' [:key])}
                                on-complete (fn [res _err]
                                              (when res
                                                (push :plugins-page/activate-plugin (:key plugin'))))
                                http-opts' (merge http-opts {:body body
                                                             :on-complete on-complete})]
                            (http/post "/api/activate-plugin" http-opts')))
        deactivate-plugin (fn [_e plugin]
                            (let [body {:plugin (select-keys plugin [:key])}
                                  on-complete (fn [res _err]
                                                (when res
                                                  (push :plugins-page/deactivate-plugin (:key plugin))))
                                  http-opts' (merge http-opts {:body body
                                                               :on-complete on-complete})]
                              (http/post "/api/deactivate-plugin" http-opts')))
        restart-server (fn [_e]
                         (let [on-complete (fn [_res _err])
                               http-opts' (merge http-opts {:on-complete on-complete})]
                           (push :plugins-page/restart-server)
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
          [:button {:class "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44"
                    :onClick restart-server}
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

(html/add-element :plugins-page
                  (admin-impl/wrap-component plugins-page)
                  {:format :transit})

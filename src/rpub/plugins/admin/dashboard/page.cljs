(ns rpub.plugins.admin.dashboard.page
  (:require [rpub.plugins.admin.helpers :as helpers]))

(defn- dashboard-content-types [{:keys [content-types]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-content-types"}
   [helpers/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M5.005 10.19a1 1 0 0 1 1 1v.233l5.998 3.464L18 11.423v-.232a1 1 0 1 1 2 0V12a1 1 0 0 1-.5.866l-6.997 4.042a1 1 0 0 1-1 0l-6.998-4.042a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1ZM5 15.15a1 1 0 0 1 1 1v.232l5.997 3.464 5.998-3.464v-.232a1 1 0 1 1 2 0v.81a1 1 0 0 1-.5.865l-6.998 4.042a1 1 0 0 1-1 0L4.5 17.824a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1Z" :clip-rule "evenodd"}]
       [:path {:d "M12.503 2.134a1 1 0 0 0-1 0L4.501 6.17A1 1 0 0 0 4.5 7.902l7.002 4.047a1 1 0 0 0 1 0l6.998-4.04a1 1 0 0 0 0-1.732l-6.997-4.042Z"}]]
      "Content Types"]
     :class "md:h-48"
     :content
     [:div
      (let [[num word] (helpers/pluralize content-types "types")]
        [:p {:class "mb-4"} "This site has " [:span {:class "font-semibold"} num] " " word " of content:"])
      [helpers/content-item-counts {:content-types content-types}]]}]])

(defn- dashboard-theme [{:keys [theme]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-theme"}
   [helpers/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z" :clip-rule "evenodd"}]]
      "Theme"]
     :class "md:h-48"
     :content
     [:div "This site is using the "
      [:a {:class "font-semibold underline"
           :href "/admin/themes"}
       (:label theme)] " theme."]}]])

(defn- dashboard-plugins [{:keys [activated-plugins]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-plugins"}
   [helpers/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]]
      "Plugins"]
     :class "md:h-48"
     :content
     [:div
      (let [[num word] (helpers/pluralize activated-plugins "plugin")]
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
   [helpers/box
    {:title [:div {:class "flex items-center"}
             [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
              [:path {:fill-rule "evenodd" :d "M9.586 2.586A2 2 0 0 1 11 2h2a2 2 0 0 1 2 2v.089l.473.196.063-.063a2.002 2.002 0 0 1 2.828 0l1.414 1.414a2 2 0 0 1 0 2.827l-.063.064.196.473H20a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-.089l-.196.473.063.063a2.002 2.002 0 0 1 0 2.828l-1.414 1.414a2 2 0 0 1-2.828 0l-.063-.063-.473.196V20a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2v-.089l-.473-.196-.063.063a2.002 2.002 0 0 1-2.828 0l-1.414-1.414a2 2 0 0 1 0-2.827l.063-.064L4.089 15H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h.09l.195-.473-.063-.063a2 2 0 0 1 0-2.828l1.414-1.414a2 2 0 0 1 2.827 0l.064.063L9 4.089V4a2 2 0 0 1 .586-1.414ZM8 12a4 4 0 1 1 8 0 4 4 0 0 1-8 0Z" :clip-rule "evenodd"}]]
             "Settings"]
     :class "md:h-48"
     :content
     [:div
      [:div
       [:span {:class "font-semibold"} "Permalinks: "]
       [:code (get-in settings [:permalink-single :value])]]]}]])

(defn- dashboard-user [{:keys [current-user]}]
  [:div {:class "w-full md:w-1/2 md:px-2 mb-4"
         :data-test-id "dashboard-user"}
   [helpers/box
    {:title
     [:div {:class "flex items-center"}
      [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
       [:path {:fill-rule "evenodd" :d "M12 20a7.966 7.966 0 0 1-5.002-1.756l.002.001v-.683c0-1.794 1.492-3.25 3.333-3.25h3.334c1.84 0 3.333 1.456 3.333 3.25v.683A7.966 7.966 0 0 1 12 20ZM2 12C2 6.477 6.477 2 12 2s10 4.477 10 10c0 5.5-4.44 9.963-9.932 10h-.138C6.438 21.962 2 17.5 2 12Zm10-5c-1.84 0-3.333 1.455-3.333 3.25S10.159 13.5 12 13.5c1.84 0 3.333-1.455 3.333-3.25S13.841 7 12 7Z" :clip-rule "evenodd"}]]
      "User"]
     :class "md:h-48"
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
     [helpers/box
      {:title
       [:div {:class "flex items-center"}
        [:svg {:class "w-8 h-8 text-gray-500 mr-4" :aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
         [:path {:fill-rule "evenodd" :d "M5 5a2 2 0 0 0-2 2v3a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1V7a2 2 0 0 0-2-2H5Zm9 2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17ZM3 17v-3a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v3a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2Zm11-2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]]
        "Server"]
       :class "md:h-48"
       :content
       [:div
        "This server is running "
        (if-not rpub-version
          [:span {:class "font-semibold"}
           "rPub from a local directory"]
          [:a {:class "font-semibold underline"
               :href rpub-url
               :target "_blank"}
           (str "rPub v" rpub-version)])
        "."]}]]))

(defn- page [props]
  [:div {:class "flex flex-wrap py-4 px-4 md:px-2"}
   [dashboard-content-types props]
   [dashboard-theme props]
   [dashboard-plugins props]
   [dashboard-settings props]
   [dashboard-user props]
   [dashboard-server props]])

(def config
  {:page-id :dashboard-page
   :component page})

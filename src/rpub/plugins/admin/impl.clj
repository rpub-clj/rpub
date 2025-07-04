(ns rpub.plugins.admin.impl)

(defn menu-item [{:keys [uri menu-item] :as props}]
  (let [selected (= uri (:href menu-item))
        test-id (str "menu-item-" (name (:key props)))
        link-attrs (cond-> {:href (:href menu-item)
                            :class "flex items-center p-2 w-full text-base font-medium text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-100"
                            :data-test-id test-id}
                     selected (update :class #(str % " bg-gray-100"))
                     (:target menu-item) (merge {:target "_blank"}))]
    [:li
     [:a link-attrs
      ((:icon menu-item) {:selected selected})
      [:span.flex-1.ml-3.text-left.whitespace-nowrap
       (:label menu-item)]]]))

(defn icon [id {:keys [selected]}]
  (let [class (if selected "text-gray-900" "text-gray-500")]
    (case id
      :plugin
      [:svg.w-6.h-6.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M5.005 10.19a1 1 0 0 1 1 1v.233l5.998 3.464L18 11.423v-.232a1 1 0 1 1 2 0V12a1 1 0 0 1-.5.866l-6.997 4.042a1 1 0 0 1-1 0l-6.998-4.042a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1ZM5 15.15a1 1 0 0 1 1 1v.232l5.997 3.464 5.998-3.464v-.232a1 1 0 1 1 2 0v.81a1 1 0 0 1-.5.865l-6.998 4.042a1 1 0 0 1-1 0L4.5 17.824a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1Z" :clip-rule "evenodd"}]
       [:path {:d "M12.503 2.134a1 1 0 0 0-1 0L4.501 6.17A1 1 0 0 0 4.5 7.902l7.002 4.047a1 1 0 0 0 1 0l6.998-4.04a1 1 0 0 0 0-1.732l-6.997-4.042Z"}]]

      :settings
      [:svg.w-6.h-6.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M9.586 2.586A2 2 0 0 1 11 2h2a2 2 0 0 1 2 2v.089l.473.196.063-.063a2.002 2.002 0 0 1 2.828 0l1.414 1.414a2 2 0 0 1 0 2.827l-.063.064.196.473H20a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-.089l-.196.473.063.063a2.002 2.002 0 0 1 0 2.828l-1.414 1.414a2 2 0 0 1-2.828 0l-.063-.063-.473.196V20a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2v-.089l-.473-.196-.063.063a2.002 2.002 0 0 1-2.828 0l-1.414-1.414a2 2 0 0 1 0-2.827l.063-.064L4.089 15H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h.09l.195-.473-.063-.063a2 2 0 0 1 0-2.828l1.414-1.414a2 2 0 0 1 2.827 0l.064.063L9 4.089V4a2 2 0 0 1 .586-1.414ZM8 12a4 4 0 1 1 8 0 4 4 0 0 1-8 0Z" :clip-rule "evenodd"}]]

      :users
      [:svg.w-6.h-6.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M8 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8Zm-2 9a4 4 0 0 0-4 4v1a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2v-1a4 4 0 0 0-4-4H6Zm7.25-2.095c.478-.86.75-1.85.75-2.905a5.973 5.973 0 0 0-.75-2.906 4 4 0 1 1 0 5.811ZM15.466 20c.34-.588.535-1.271.535-2v-1a5.978 5.978 0 0 0-1.528-4H18a4 4 0 0 1 4 4v1a2 2 0 0 1-2 2h-4.535Z" :clip-rule "evenodd"}]]

      :plugins
      [:svg.w-6.h-6.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]]

      :help
      [:svg.w-6.h-6.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm9.008-3.018a1.502 1.502 0 0 1 2.522 1.159v.024a1.44 1.44 0 0 1-1.493 1.418 1 1 0 0 0-1.037.999V14a1 1 0 1 0 2 0v-.539a3.44 3.44 0 0 0 2.529-3.256 3.502 3.502 0 0 0-7-.255 1 1 0 0 0 2 .076c.014-.398.187-.774.48-1.044Zm.982 7.026a1 1 0 1 0 0 2H12a1 1 0 1 0 0-2h-.01Z" :clip-rule "evenodd"}]]

      :dashboard
      [:svg.w-6.h-6.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:d "M5 3a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2H5Zm14 18a2 2 0 0 0 2-2v-2a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h4ZM5 11a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2H5Zm14 2a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h4Z"}]]

      :content-type
      [:svg
       {:class (str "flex-shrink-0 w-6 h-6 text-gray-500 transition duration-75 group-hover:text-gray-900"
                    class)
        :aria-hidden "true" :fill "currentColor" :viewBox "0 0 20 20" :xmlns "http://www.w3.org/2000/svg"}
       [:path {:fill-rule "evenodd" :d "M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" :clip-rule "evenodd"}]]

      :themes
      [:svg.w-6.h-6.text-gray-500 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z" :clip-rule "evenodd"}]])))

(defn menu [{:keys [menu-items] :as req}]
  [:aside#drawer-navigation
   {:class "fixed top-0 left-0 z-40 w-64 h-screen pt-14 transition-transform -translate-x-full bg-white border-r border-gray-200 md:translate-x-0"
    :aria-label "Sidenav"}
   [:div.overflow-y-auto.py-5.px-3.h-full.bg-white
    [:ul.space-y-2
     (menu-item (merge req {:key ::dashboard
                            :menu-item {:label "Dashboard"
                                        :href "/admin"
                                        :icon #(icon :dashboard %)}}))
     (for [content-type-menu-item (:content-types menu-items)]
       (menu-item (merge req {:key (str "content-type-" (:id content-type-menu-item))
                              :menu-item {:label (:label content-type-menu-item)
                                          :href (:href content-type-menu-item)
                                          :icon #(icon :content-type %)}})))]
    [:ul.pt-5.mt-5.space-y-2.border-t.border-gray-200
     (for [plugin (sort-by :label (:plugins menu-items))
           :let [plugin' (assoc plugin :icon #(icon :plugin %))]]
       (menu-item (merge req {:key (:label plugin')
                              :menu-item plugin'})))]
    [:ul.pt-5.mt-5.space-y-2.border-t.border-gray-200
     (menu-item (merge req {:key ::users
                            :menu-item {:label "Users"
                                        :href "/admin/users"
                                        :icon #(icon :users %)}}))
     (menu-item (merge req {:key ::settings
                            :menu-item {:label "Settings"
                                        :href "/admin/settings"
                                        :icon #(icon :settings %)}}))
     (menu-item (merge req {:key ::themes
                            :menu-item {:label "Themes"
                                        :href "/admin/themes"
                                        :icon #(icon :themes %)}}))
     (menu-item (merge req {:key ::plugins
                            :menu-item {:label "Plugins"
                                        :href "/admin/plugins"
                                        :icon #(icon :plugins %)}}))]
    [:ul.pt-5.mt-5.space-y-2.border-t.border-gray-200
     (menu-item (merge req {:key ::help
                            :menu-item {:label "Help"
                                        :href "https://rpub.dev"
                                        :target "_blank"
                                        :icon #(icon :help %)}}))]]])

(defn main [{:keys [current-page] :as _req}]
  [:main {:class "md:ml-64 h-auto pt-12"}
   (:primary current-page)])

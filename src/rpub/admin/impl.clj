(ns rpub.admin.impl
  (:require [babashka.json :as json]
            [buddy.auth.backends :as buddy-backends]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup2.core :as hiccup]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.defaults :as defaults]
            [ring.util.response :as response]
            [rpub.lib.html :as html]
            [rpub.lib.plugins :as plugins]
            [rpub.lib.ring :as ring]
            [rpub.model :as model]))

(defn view-site-button [{:keys [site-base-url] :as _settings}]
  [:a.pl-4.h-full.inline-block.border-l.border-gray-200.font-semibold.flex.items-center.hover:underline
   {:href "/"
    :target "_blank"
    :data-test-id "view-site-button"}
   [:div.mr-1 (if-let [v (:value site-base-url)]
                (str/replace v #"^https?://" "")
                "Go to Site")]
   [:svg.w-6.h-6.text-gray-500.dark:text-white.ml-0 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "none" :viewBox "0 0 24 24"}
    [:path {:stroke "currentColor" :stroke-linecap "round" :stroke-linejoin "round" :stroke-width "2" :d "M19 12H5m14 0-4 4m4-4-4-4"}]]])

(defn site-title [settings]
  [:span.px-4.h-full.inline-block.border-l.border-gray-200.font-semibold.flex.items-center
   [:svg.w-6.h-8.text-gray-500.dark:text-white.mr-2 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
    [:path {:fill-rule "evenodd" :d "M11.293 3.293a1 1 0 0 1 1.414 0l6 6 2 2a1 1 0 0 1-1.414 1.414L19 12.414V19a2 2 0 0 1-2 2h-3a1 1 0 0 1-1-1v-3h-2v3a1 1 0 0 1-1 1H7a2 2 0 0 1-2-2v-6.586l-.293.293a1 1 0 0 1-1.414-1.414l2-2 6-6Z" :clip-rule "evenodd"}]]
   (get-in settings [:site-title :value])])

(defn user-info [{:keys [current-user anti-forgery-token]}]
  [:div.flex.items-center.lg:order-2
   [:div.border-r.pr-4.border-gray-200.flex.items-center
    [:svg.w-8.h-8.text-gray-500.dark:text-white.mr-2 {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24"}
     [:path {:fill-rule "evenodd" :d "M12 20a7.966 7.966 0 0 1-5.002-1.756l.002.001v-.683c0-1.794 1.492-3.25 3.333-3.25h3.334c1.84 0 3.333 1.456 3.333 3.25v.683A7.966 7.966 0 0 1 12 20ZM2 12C2 6.477 6.477 2 12 2s10 4.477 10 10c0 5.5-4.44 9.963-9.932 10h-.138C6.438 21.962 2 17.5 2 12Zm10-5c-1.84 0-3.333 1.455-3.333 3.25S10.159 13.5 12 13.5c1.84 0 3.333-1.455 3.333-3.25S13.841 7 12 7Z" :clip-rule "evenodd"}]]
    (:username current-user)]
   [:form.pl-4 {:method :post, :action "/admin/logout"}
    [:input {:type :hidden :name :__anti-forgery-token :value anti-forgery-token}]
    [:button.hover:underline {:type "submit"} "Log Out"]]])

(defn menu-item [{:keys [uri menu-item] :as props}]
  (let [selected (= uri (:href menu-item))
        test-id (str "menu-item-" (name (:key props)))
        link-attrs (cond-> {:href (:href menu-item)
                            :data-test-id test-id}
                     selected (assoc :class "bg-gray-100")
                     (:target menu-item) (merge {:target "_blank"}))]
    [:li
     [:a.flex.items-center.p-2.w-full.text-base.font-medium.text-gray-900.rounded-lg.transition.duration-75.group.hover:bg-gray-100.dark:text-white.dark:hover:bg-gray-700
      link-attrs
      ((:icon menu-item) {:selected selected})
      [:span.flex-1.ml-3.text-left.whitespace-nowrap
       (:label menu-item)]]]))

(defn icon [id {:keys [selected]}]
  (let [class (if selected "text-gray-900" "text-gray-500")]
    (case id
      :plugin
      [:svg.w-6.h-6.text-gray-500.dark:text-white {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M5.005 10.19a1 1 0 0 1 1 1v.233l5.998 3.464L18 11.423v-.232a1 1 0 1 1 2 0V12a1 1 0 0 1-.5.866l-6.997 4.042a1 1 0 0 1-1 0l-6.998-4.042a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1ZM5 15.15a1 1 0 0 1 1 1v.232l5.997 3.464 5.998-3.464v-.232a1 1 0 1 1 2 0v.81a1 1 0 0 1-.5.865l-6.998 4.042a1 1 0 0 1-1 0L4.5 17.824a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1Z" :clip-rule "evenodd"}]
       [:path {:d "M12.503 2.134a1 1 0 0 0-1 0L4.501 6.17A1 1 0 0 0 4.5 7.902l7.002 4.047a1 1 0 0 0 1 0l6.998-4.04a1 1 0 0 0 0-1.732l-6.997-4.042Z"}]]

      :settings
      [:svg.w-6.h-6.text-gray-500.dark:text-white {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M9.586 2.586A2 2 0 0 1 11 2h2a2 2 0 0 1 2 2v.089l.473.196.063-.063a2.002 2.002 0 0 1 2.828 0l1.414 1.414a2 2 0 0 1 0 2.827l-.063.064.196.473H20a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-.089l-.196.473.063.063a2.002 2.002 0 0 1 0 2.828l-1.414 1.414a2 2 0 0 1-2.828 0l-.063-.063-.473.196V20a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2v-.089l-.473-.196-.063.063a2.002 2.002 0 0 1-2.828 0l-1.414-1.414a2 2 0 0 1 0-2.827l.063-.064L4.089 15H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h.09l.195-.473-.063-.063a2 2 0 0 1 0-2.828l1.414-1.414a2 2 0 0 1 2.827 0l.064.063L9 4.089V4a2 2 0 0 1 .586-1.414ZM8 12a4 4 0 1 1 8 0 4 4 0 0 1-8 0Z" :clip-rule "evenodd"}]]

      :users
      [:svg.w-6.h-6.text-gray-500.dark:text-white {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M8 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8Zm-2 9a4 4 0 0 0-4 4v1a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2v-1a4 4 0 0 0-4-4H6Zm7.25-2.095c.478-.86.75-1.85.75-2.905a5.973 5.973 0 0 0-.75-2.906 4 4 0 1 1 0 5.811ZM15.466 20c.34-.588.535-1.271.535-2v-1a5.978 5.978 0 0 0-1.528-4H18a4 4 0 0 1 4 4v1a2 2 0 0 1-2 2h-4.535Z" :clip-rule "evenodd"}]]

      :plugins
      [:svg.w-6.h-6.text-gray-500.dark:text-white {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z" :clip-rule "evenodd"}]]

      :help
      [:svg.w-6.h-6.text-gray-500.dark:text-white {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm9.008-3.018a1.502 1.502 0 0 1 2.522 1.159v.024a1.44 1.44 0 0 1-1.493 1.418 1 1 0 0 0-1.037.999V14a1 1 0 1 0 2 0v-.539a3.44 3.44 0 0 0 2.529-3.256 3.502 3.502 0 0 0-7-.255 1 1 0 0 0 2 .076c.014-.398.187-.774.48-1.044Zm.982 7.026a1 1 0 1 0 0 2H12a1 1 0 1 0 0-2h-.01Z" :clip-rule "evenodd"}]]

      :dashboard
      [:svg.w-6.h-6.text-gray-500.dark:text-white {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:d "M5 3a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2H5Zm14 18a2 2 0 0 0 2-2v-2a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2a2 2 0 0 0 2 2h4ZM5 11a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2H5Zm14 2a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h4Z"}]]

      :content-type
      [:svg.flex-shrink-0.w-6.h-6.text-gray-500.transition.duration-75.group-hover:text-gray-900.dark:text-gray-400.dark:group-hover:text-white {:aria-hidden "true" :fill "currentColor" :viewBox "0 0 20 20" :xmlns "http://www.w3.org/2000/svg" :class class}
       [:path {:fill-rule "evenodd" :d "M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" :clip-rule "evenodd"}]]

      :themes
      [:svg.w-6.h-6.text-gray-500.dark:text-white {:aria-hidden "true" :xmlns "http://www.w3.org/2000/svg" :width "24" :height "24" :fill "currentColor" :viewBox "0 0 24 24" :class class}
       [:path {:fill-rule "evenodd" :d "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z" :clip-rule "evenodd"}]
       [:path {:fill-rule "evenodd" :d "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z" :clip-rule "evenodd"}]])))

(defn menu [{:keys [menu-items] :as req}]
  [:aside#drawer-navigation.fixed.top-0.left-0.z-40.w-64.h-screen.pt-14.transition-transform.-translate-x-full.bg-white.border-r.border-gray-200.md:translate-x-0.dark:bg-gray-800.dark:border-gray-700 {:aria-label "Sidenav"}
   [:div.overflow-y-auto.py-5.px-3.h-full.bg-white.dark:bg-gray-800
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
    [:ul.pt-5.mt-5.space-y-2.border-t.border-gray-200.dark:border-gray-700
     (for [plugin (:plugins menu-items)
           :let [plugin' (assoc plugin :icon #(icon :plugin %))]]
       (menu-item (merge req {:key (:label plugin')
                              :menu-item plugin'})))]
    [:ul.pt-5.mt-5.space-y-2.border-t.border-gray-200.dark:border-gray-70
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
    [:ul.pt-5.mt-5.space-y-2.border-t.border-gray-200.dark:border-gray-700
     (menu-item (merge req {:key ::help
                            :menu-item {:label "Help"
                                        :href "https://rpub.dev"
                                        :target "_blank"
                                        :icon #(icon :help %)}}))]]])

(defn logo [{:keys [class]}]
  [:span.self-center.font-semibold.font-app-serif.whitespace-nowrap.dark:text-white
   {:class class}
   [:span.text-blue-500.italic "r"] "Pub"])

(defn header [{:keys [settings] :as req}]
  [:nav.bg-white.border-b.border-gray-200.px-4.py-2.5.dark:bg-gray-800.dark:border-gray-700.fixed.left-0.right-0.top-0.z-50
   [:div.flex.flex-wrap.justify-between.items-center
    [:div.flex.justify-start.items-center
     [:button.p-2.mr-2.text-gray-600.rounded-lg.cursor-pointer.md:hidden.hover:text-gray-900.hover:bg-gray-100.focus:bg-gray-100.dark:focus:bg-gray-700.focus:ring-2.focus:ring-gray-100.dark:focus:ring-gray-700.dark:text-gray-400.dark:hover:bg-gray-700.dark:hover:text-white {:data-drawer-target "drawer-navigation" :data-drawer-toggle "drawer-navigation" :aria-controls "drawer-navigation"}
      [:svg.w-6.h-6 {:aria-hidden "true" :fill "currentColor" :viewBox "0 0 20 20" :xmlns "http://www.w3.org/2000/svg"}
       [:path {:fill-rule "evenodd" :d "M3 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM3 10a1 1 0 011-1h6a1 1 0 110 2H4a1 1 0 01-1-1zM3 15a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" :clip-rule "evenodd"}]]
      [:svg.hidden.w-6.h-6 {:aria-hidden "true" :fill "currentColor" :viewBox "0 0 20 20" :xmlns "http://www.w3.org/2000/svg"}
       [:pa/h {:fill-rule "evenodd" :d "M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" :clip-rule "evenodd"}]]
      [:span.sr-only "Toggle sidebar"]]
     [:a.mr-4 {:href "/admin"}
      (logo {:class "text-2xl"})]
     (site-title settings)
     (view-site-button settings)]
    (user-info req)]])

(defn main [{:keys [current-page] :as _req}]
  [:main.md:ml-64.h-auto.pt-12
   (:primary current-page)])

(defn- module-preloads [import-map]
  (for [[_ src] (set (:imports import-map))]
    [:link {:rel "modulepreload" :href src}]))

(defn import-script [{:keys [cljs-repl]}]
  (if cljs-repl
    "import * as cljsClient from 'rpub.dev.cljs.client';
    cljsClient.start_BANG_();"
    "import 'rpub.admin';"))

(defn layout [{:keys [title content head cljs import-map] :as req}]
  (str
    (hiccup/html
      {:mode :html}
      (hiccup/raw "<!DOCTYPE html>")
      [:html {:lang "en"}
       [:head
        [:meta {:charset "utf-8"}]
        [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
        (when cljs
          (list
            [:meta {:name "csrf-token" :content (force anti-forgery/*anti-forgery-token*)}]
            [:script {:type "importmap"} (hiccup/raw (json/write-str import-map))]
            (module-preloads import-map)
            [:script {:type "module"} (hiccup/raw (import-script req))]))
        head
        [:title title]
        (html/stylesheet-tag "admin/main.css")]
       [:body
        content]])))

(defn admin-content [req]
  [:div.antialiased.bg-gray-50.dark:bg-gray-900
   (header req)
   (menu req)
   (main req)])

(defn admin-title [_]
  "rPub Admin")

(defn login-page? [req]
  (str/starts-with? (:uri req) "/admin/login"))

(defn setup-page? [req]
  (str/starts-with? (:uri req) "/admin/setup"))

(defn empty-content [{:keys [current-page]}]
  [:div.p-8
   [:div.mx-auto.max-w-5xl (:primary current-page)]])

(defn ->plugin-menu-items [{:keys [admin-menu-items] :as _req}]
  (apply merge-with concat admin-menu-items))

(defn page-response [{:keys [current-user head import-map cljs-repl] :as req} current-page]
  (let [cljs (get current-page :cljs true)
        req' (merge req {:current-page current-page
                         :current-user current-user
                         :site-title (get-in req [:settings :site-title :value])
                         :menu-items (->plugin-menu-items req)})
        title (admin-title req')
        content (if (or (login-page? req') (setup-page? req'))
                  (empty-content req')
                  (admin-content req'))
        body (layout {:cljs cljs
                      :cljs-repl cljs-repl
                      :title title
                      :content content
                      :head head
                      :import-map import-map})]
    (response/response body)))

(defn wrap-no-cache [handler]
  (fn [req]
    (-> (handler req)
        (update :headers merge
                {"Cache-Control" "no-cache, no-store, must-revalidate"
                 "Pragma" "no-cache"
                 "Expires" "0"}))))

(defn read-js-manifest []
  (slurp (io/resource "public/js/manifest.json")))

(defn js-manifest->import-map [js-manifest]
  (->> (json/read-str js-manifest {:key-fn str})
       (map (fn [[from to]]
              [(-> from
                   (str/replace #"^rpub/[^/]+/" "")
                   (str/replace #"\.js$" "")
                   (str/replace "/" ".")
                   (str/replace "_" "-"))
               (str "/js/" to)]))
       (into {})))

(defn- cherry-imports [{:keys [cljs-repl] :as _req}]
  (cond-> {"cherry-cljs/cljs.core.js" "https://cdn.jsdelivr.net/npm/cherry-cljs@0.4.26/cljs.core.js"
           "cherry-cljs/lib/clojure.walk.js" "https://cdn.jsdelivr.net/npm/cherry-cljs@0.4.26/lib/clojure.walk.js"
           "cherry-cljs/lib/clojure.set.js" "https://cdn.jsdelivr.net/npm/cherry-cljs@0.4.26/lib/clojure.set.js"
           "cherry-cljs/lib/clojure.string.js" "https://cdn.jsdelivr.net/npm/cherry-cljs@0.4.26/lib/clojure.string.js"}
    cljs-repl (merge {"cherry-cljs/lib/cljs.pprint.js" "https://cdn.jsdelivr.net/npm/cherry-cljs@0.4.26/lib/cljs.pprint.js"
                      "cherry-cljs/lib/compiler.js" "https://cdn.jsdelivr.net/npm/cherry-cljs@0.4.26/lib/compiler.js"})))

(defn- flowbite-imports []
  {"flowbite" "https://cdn.jsdelivr.net/npm/flowbite@2.5.2/dist/flowbite.min.js"})

(defn- transit-js-imports []
  {"transit-js" "https://cdn.jsdelivr.net/npm/transit-js@0.8.874/transit.js/+esm"})

(defn- preact-imports [{:keys [cljs-repl] :as _req}]
  (cond->
    {"preact" "https://cdn.jsdelivr.net/npm/preact@10.25.0/dist/preact.module.js"
     "preact/compat" "https://cdn.jsdelivr.net/npm/preact@10.25.0/compat/dist/compat.module.js"
     "preact/devtools" "https://cdn.jsdelivr.net/npm/preact@10.25.0/devtools/dist/devtools.module.js"
     "preact/hooks" "https://cdn.jsdelivr.net/npm/preact@10.25.0/hooks/dist/hooks.module.js"
     "react" "https://cdn.jsdelivr.net/npm/preact@10.25.0/compat/dist/compat.module.js"
     "react/jsx-runtime" "https://cdn.jsdelivr.net/npm/preact@10.25.0/jsx-runtime/dist/jsxRuntime.module.js"}
    cljs-repl (merge {"preact/debug" "https://cdn.jsdelivr.net/npm/preact@10.25.0/debug/dist/debug.module.js"})))

(defn- rads-imports []
  {"rads.inflections" "/js/rads/inflections-v0.14.2-1.min.js"
   "rads.dependency" "/js/rads/dependency-v1.0.0-1.min.js"})

(defn- js-manifest-imports []
  (js-manifest->import-map (read-js-manifest)))

(defn- npm-imports [req]
  (merge (cherry-imports req)
         (flowbite-imports)
         (transit-js-imports)
         (preact-imports req)))

(defn- load-import-map [req]
  {:imports (merge (npm-imports req)
                   (rads-imports)
                   (js-manifest-imports))})

(defn- wrap-import-map [handler]
  (let [cached-import-map (atom nil)]
    (fn [{:keys [reload] :as req}]
      (let [import-map (if reload
                         (load-import-map req)
                         (do
                           (when-not @cached-import-map
                             (let [v (load-import-map req)]
                               (compare-and-set! cached-import-map nil v)))
                           @cached-import-map))
            req' (assoc req :import-map import-map)]
        (handler req')))))

(defn- csp-extra-script-src [{:keys [import-map] :as req}]
  ["https://cdn.jsdelivr.net"
   (ring/script-hash (json/write-str import-map))
   (ring/script-hash (import-script req))])

(defn admin-middleware
  [{:keys [content-security-policy ::defaults] :as opts}]
  (let [opts' (merge {:auth-required true, :tap true} opts)
        defaults' (or defaults (ring/site-defaults opts'))
        {:keys [auth-required tap]} opts']
    (concat
      [[defaults/wrap-defaults defaults']]
      (when auth-required
        (ring/auth-middleware
          {:auth-backend (buddy-backends/session
                           {:unauthorized-handler ring/unauthorized-handler})
           :get-current-user model/get-current-user}))
      (plugins/plugin-middleware opts)
      (when content-security-policy
        [[ring/wrap-content-security-policy
          {:extra-script-src csp-extra-script-src}]])
      [wrap-no-cache
       wrap-import-map]
      (when tap [ring/wrap-tap]))))

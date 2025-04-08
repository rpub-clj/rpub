(ns rpub.plugins.content-types.admin.single-content-type-page
  (:require [rads.inflections :as inflections]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]
            [rpub.lib.http :as http]))

(def months
  ["January" "February" "March" "April" "May" "June"
   "July" "August" "September" "October" "November" "December"])

(defn- format-datetime [date]
  (let [month-idx (.getMonth date)
        day (.getDate date)
        year (.getFullYear date)
        hours (.getHours date)
        minutes (.getMinutes date)
        month (get months month-idx)
        meridiem (if (< hours 12) "AM" "PM")
        h (mod hours 12)
        display-hours (if (zero? h) 12 h)
        display-minutes (if (< minutes 10) (str "0" minutes) (str minutes))]
    (str month " " day ", " year " " display-hours ":" display-minutes " " meridiem)))

(defn edit-content-item-path [content-item]
  (str "/admin/content-types/"
       (name (get-in content-item [:content-type :slug]))
       "/content-items/"
       (name (get-in content-item [:fields "Slug"]))))

(def columns
  [{:name "Title"
    :value (fn [{:keys [fields] :as content-item}]
             [:a.underline {:href (edit-content-item-path content-item)}
              [:span {:class "font-semibold"}
               (get fields "Title")]])}

   {:name "Author"
    :value
    (fn [{:keys [created-by]}]
      (let [{:keys [username]} created-by]
        [:span {:class "font-semibold"}
         username]))}

   {:name "Date"
    :value (fn [{:keys [created-at updated-at]}]
             (some-> (or updated-at created-at)
                     js/Date.
                     format-datetime))}])

(defn new-content-item-path [content-type]
  (str "/admin/content-types/"
       (name (:slug content-type))
       "/content-items/new"))

(defn single-content-type-page [{:keys [content-type content-items]}]
  (let [content-items (map (fn [content-item]
                             (update content-item :fields #(update-keys % name)))
                           content-items)
        content-items' (->> content-items (map #(assoc % :content-type content-type)))]
    [:div {:class "p-4"}
     [admin-impl/table
      {:title (inflections/plural (:name content-type))
       :columns columns
       :rows content-items'
       :header-buttons [:a {:href (new-content-item-path content-type)}
                        [html/action-button
                         (str "New " (inflections/singular (:name content-type)))]]}]]))

(def config
  {:page-id :single-content-type-page
   :component single-content-type-page})

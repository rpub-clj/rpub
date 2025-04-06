(ns rpub.plugins.content-types.admin.single-content-item-page
  (:require [rads.inflections :as inflections]
            [rpub.admin.impl :as admin-impl]
            [rpub.lib.html :as html]))

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

(defn field-type-label [field]
  (:name field))

(defn rpub-field-types-text
  [{:keys [field editing creating value on-change] :as _props}]
  [html/input2 (cond-> {:type :text
                        :name (:name field)
                        :on-change on-change}
                 editing (assoc :value value)
                 creating (assoc :placeholder (field-type-label field)))])

(defn rpub-field-types-text-lg [{:keys [field editing creating value]}]
  [html/textarea (cond-> {:name (:name field)
                          :on-change prn}
                   editing (assoc :value value)
                   creating (assoc :placeholder (field-type-label field)))])

(defn rpub-field-types-choice [_]
  [:select])

(defn rpub-field-types-datetime [_]
  [:input {:type :datetime-local}])

(defn rpub-field-types-number [_]
  [:input {:type :number}])

(defn- content-item-fields [{:keys [content-item editing creating field-types]}]
  (let [{:keys [content-type]} content-item]
    [:div
     (for [field (sort-by :rank (:fields content-type))
           :let [v (get-in content-item [:document (:id field)])
                 v' (if (= v ::new-field) "" v)]]
       [:div.max-w-xl.mb-4 {:key (:id field)}
        [:label.font-semibold.mb-1.block {:for (:name field)}
         (:name field)]
        [:div
         (html/custom-element
           [(get-in field-types [(:type field) :input])
            {:field field
             :value v'
             :on-change #(prn (-> % .-target .-value))
             :editing editing
             :creating creating}])]])]))

(defn new-content-item-page [{:keys [content-type field-types]}]
  (let [content-item {:content-type content-type
                      :document (->> (:fields content-type)
                                     (map (fn [field] [(:id field) ::new-field]))
                                     (into {}))}
        submitting false]
    [:div.p-4
     [admin-impl/box
      {:class "mb-4"
       :title (str "New " (inflections/singular (get-in content-item [:content-type :name])))}]
     [admin-impl/box
      {:content
       [:div
        [content-item-fields {:content-item content-item
                              :field-types field-types
                              :creating true}]
        [html/submit-button {:ready-label "Create"
                             :submit-label "Creating..."
                             :submitting submitting}]]}]]))

(defn edit-content-item-page [{:keys [content-type content-item field-types]}]
  (let [submitting false]
    [:div.p-4
     [admin-impl/box
      {:class "mb-4"
       :title
       [:div
        [:span.italic.text-blue-600
         (str (inflections/singular (:name content-type)) ": ")]
        (get-in content-item [:fields "Title"])]}]
     [admin-impl/box
      {:content
       [:div
        [content-item-fields {:content-item content-item
                              :field-types field-types
                              :editing true}]
        [html/submit-button {:ready-label "Save"
                             :submit-label "Saving..."
                             :submitting submitting}]]}]]))

(defn single-content-item-page [{:keys [editing] :as props}]
  (if editing
    [edit-content-item-page props]
    [new-content-item-page props]))

(def config
  {:page-id :single-content-item-page
   :component single-content-item-page})

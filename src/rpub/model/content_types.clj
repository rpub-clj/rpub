(ns rpub.model.content-types
  (:require [rpub.model :as model]))

^:clj-reload/keep
(defprotocol Model
  (migrate! [model])
  (get-content-types [model opts])
  (get-content-items [model opts])
  (create-content-type! [model opts])
  (update-content-type! [model opts])
  (delete-content-type! [model opts])
  (create-content-item! [model opts])
  (update-content-item! [model opts])
  (delete-content-item! [model opts]))

(def slug-field-id #uuid"6bd0ff7a-b720-4972-b98a-2aa85d179357")
(def title-field-id #uuid"cd334826-1ec6-4906-8e7f-16ece1865faf")
(def content-field-id #uuid"65a6aa2e-73a3-4283-afe1-58e610d6727d")

(defmulti ->model :db-type)

(defn ->field [{:keys [current-user name type rank] :as opts}]
  (as-> {:id (or (:id opts) (random-uuid))
         :name name
         :type type
         :rank rank} $
    (model/add-metadata $ current-user)))

(defn ->content-type [{:keys [current-user name slug fields] :as opts}]
  (let [fields (map #(->field (assoc % :current-user current-user)) fields)]
    (as-> {:id (or (:id opts) (random-uuid))
           :name name
           :slug slug
           :fields fields} $
      (model/add-metadata $ current-user))))

(defn ->content-item [{:keys [current-user content-type document] :as opts}]
  (as-> {:id (or (:id opts) (random-uuid))
         :document document
         :content-type content-type} $
    (model/add-metadata $ current-user)))

(defn default-content-types [{:keys [current-user] :as _opts}]
  (let [title-field {:id title-field-id, :name "Title", :type :text, :rank 1}
        slug-field {:id slug-field-id, :name "Slug", :type :text, :rank 2}
        content-field {:id content-field-id, :name "Content", :type :text-lg, :rank 3}]
    [(->content-type
       {:id (random-uuid)
        :name "Pages"
        :slug :pages
        :current-user current-user
        :fields [title-field
                 slug-field
                 content-field]})
     (->content-type
       {:id (random-uuid)
        :name "Posts"
        :slug :posts
        :current-user current-user
        :fields [title-field
                 slug-field
                 content-field]})]))

(defn- content-type-field-id [content-type field-name]
  (:id (some #(when (= (:name %) field-name) %) (:fields content-type))))

(def initial-post
  {:title "Hello World!"
   :slug "hello-world"
   :content "This is the first post."})

(def initial-page
  {:title "About"
   :slug "about"
   :content "This is the about page."})

(defn- content-type-by-slug [content-types slug]
  (some #(when (= (:slug %) slug) %) content-types))

(defn default-content-items [content-types {:keys [current-user]}]
  (let [posts-type (content-type-by-slug content-types :posts)
        pages-type (content-type-by-slug content-types :pages)]
    (concat
      [(->content-item
         {:id (random-uuid)
          :current-user current-user
          :content-type posts-type
          :document {(content-type-field-id posts-type "Title")
                     (:title initial-post)

                     (content-type-field-id posts-type "Slug")
                     (:slug initial-post)

                     (content-type-field-id posts-type "Content")
                     (:content initial-post)}})
       (->content-item
         {:id (random-uuid)
          :current-user current-user
          :content-type pages-type
          :document {(content-type-field-id pages-type "Title")
                     (:title initial-page)

                     (content-type-field-id pages-type "Slug")
                     (:slug initial-page)

                     (content-type-field-id pages-type "Content")
                     (:content initial-page)}})])))

(defn seed! [model]
  (let [content-types (default-content-types model)
        content-items (default-content-items content-types model)]
    (doseq [content-type content-types]
      (create-content-type! model content-type))
    (doseq [content-item content-items]
      (create-content-item! model content-item))))

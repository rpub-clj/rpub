(ns rpub.plugins.content-types.model
  {:no-doc true})

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

(defmulti ->model :db-type)

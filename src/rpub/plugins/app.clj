(ns rpub.plugins.app
  (:require [rpub.app :as app]
            [rpub.model :as model]))

(defmethod model/internal-plugin ::plugin [_]
  {:routes app/routes
   :wildcard true})

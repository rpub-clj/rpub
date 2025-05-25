(ns rpub.plugins.admin
  (:require [rpub.admin :as admin]
            [rpub.model :as model]))

(defmethod model/internal-plugin ::plugin [_]
  {:routes admin/routes})

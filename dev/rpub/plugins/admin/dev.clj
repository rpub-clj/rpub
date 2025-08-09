(ns rpub.plugins.admin.dev
  {:no-doc true}
  (:require [rpub.lib.tap :as tap]
            [rpub.plugins.admin.helpers :as helpers]))

(defn page-response [req current-page]
  (helpers/page-response req current-page))

(defn admin-middleware [opts]
  (helpers/admin-middleware opts))

(defn routes [opts]
  [["" {:middleware (helpers/admin-middleware (assoc opts :tap false))}
    ["/admin/api/tap" {:post #'tap/handler}]]])

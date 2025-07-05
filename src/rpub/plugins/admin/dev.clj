(ns rpub.plugins.admin.dev
  {:no-doc true}
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [rads.inflections :as inflections]
            [ring.util.response :as response]
            [rpub.lib.tap :as tap]
            [rpub.model.app :as-alias model-app]
            [rpub.plugins.admin.helpers :as helpers]))

(defn page-response [req current-page]
  (helpers/page-response req current-page))

(defn admin-middleware [opts]
  (helpers/admin-middleware opts))

(defn dag-metadata-path [storage-id]
  (format "dev/rpub/dev/dag/metadata/%s.edn"
          (inflections/parameterize (str storage-id))))

(defn get-dag-metadata-handler [{:keys [body-params] :as _req}]
  (let [storage-id (get body-params :storage-id)
        saved-nodes (when (.exists (io/file (dag-metadata-path storage-id)))
                      (-> (slurp (dag-metadata-path storage-id))
                          edn/read-string
                          :saved-nodes))]
    (response/response {:saved-nodes saved-nodes})))

(defn- update-dag-metadata-handler [{:keys [body-params] :as _req}]
  (let [storage-id (:key body-params)]
    (spit (dag-metadata-path storage-id) (pr-str (:value body-params)))
    (response/response {:success true})))

(defn routes [opts]
  [["" {:middleware (helpers/admin-middleware (assoc opts :tap false))}
    ["/admin/api/get-dag-metadata" {:post #'get-dag-metadata-handler}]
    ["/admin/api/update-dag-metadata" {:post #'update-dag-metadata-handler}]
    ["/admin/api/tap" {:post #'tap/handler}]]])

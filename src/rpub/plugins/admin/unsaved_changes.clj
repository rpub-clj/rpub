(ns rpub.plugins.admin.unsaved-changes
  {:no-doc true}
  (:require [ring.util.response :as response]
            [rpub.model :as model]
            [rpub.model.app :as-alias model-app]))

(defn- last-updated [unsaved-changes]
  (->> unsaved-changes
       (sort-by #(or (:updated-at %) (:created-at %)) #(compare %2 %1))
       first))

(defn- update-unsaved-changes-handler
  [{:keys [model current-user body-params] :as _req}]
  (let [existing-unsaved-changes (-> (model/get-unsaved-changes
                                       model
                                       {:user-ids [(:id current-user)]
                                        :keys [(:key body-params)]})
                                     last-updated)
        updated-unsaved-changes (model/->unsaved-changes
                                  (-> (if existing-unsaved-changes
                                        (assoc existing-unsaved-changes :value (:value body-params))
                                        body-params)
                                      (dissoc :id)
                                      (assoc :current-user current-user)
                                      (assoc :client-id (:client-id body-params))))]
    (model/update-unsaved-changes! model updated-unsaved-changes)
    (response/response {:success true})))

(defn routes [{:keys [admin-middleware]}]
  [["" {:middleware admin-middleware}
    ["/admin/api/update-unsaved-changes" {:post #'update-unsaved-changes-handler}]]])

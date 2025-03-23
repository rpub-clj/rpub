(ns rpub.plugins.content-types.rest
  {:no-doc true}
  (:require [ring.util.response :as response]
            [rpub.api.impl :as api-impl]))

(defn list-content-items-handler [_req]
  (response/response 200))

(defn update-content-item-handler [_req]
  (response/response 200))

(defn delete-content-item-handler [_req]
  (response/response 200))

(defn get-content-item-handler [_req]
  (response/response 200))

(defn create-content-item-handler [_req]
  (response/response 200))

(defn routes [opts]
  ["/api/rest" {:middleware (api-impl/api-middleware opts)}
   ["/{plural-api-id}" {:get list-content-items-handler
                        :post create-content-item-handler}]
   ["/{plural-api-id}/{document-id}" {:get get-content-item-handler
                                      :put update-content-item-handler
                                      :delete delete-content-item-handler}]])

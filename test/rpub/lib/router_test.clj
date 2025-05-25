(ns rpub.lib.router-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [muuntaja.core :as m]
            [ring.util.response :as response]
            [rpub.lib.router :as router]))

(def admin
  {:id :admin
   :activated true
   :routes
   (fn [_]
     [["/admin"
       (fn [_]
         (response/response "Dashboard"))]])})

(def plugin-1
  {:id :plugin-1
   :activated true
   :routes
   (fn [_]
     [["/admin/plugin-1"
       (fn [_]
         (response/response "Plugin 1"))]])})

(def app
  {:id :app
   :activated true
   :wildcard true
   :routes
   (fn [_]
     [["*path"
       (fn [{:keys [uri] :as _req}]
         (cond
           (= uri "/")
           (response/response "Home")

           (str/starts-with? uri "/posts/")
           (response/response "Permalinks")))]])})

(defn test-request [{:keys [plugins]}]
  {:server-port 8080
   :server-name "localhost"
   :remote-addr "127.0.0.1"
   :uri "/"
   :scheme :http
   :request-method :post
   :plugins plugins
   :protocol "HTTP/1.1"})

(defn not-found [_]
  {:status 404, :body "", :headers {}})

(deftest router-test
  (let [middleware []
        plugins [admin plugin-1 app]
        req (test-request {:plugins plugins})
        handler (router/ring-handler
                  {:muuntaja m/instance
                   :middleware middleware
                   :not-found not-found})
        res (handler req)]
    (is (= 200 (:status res)))))

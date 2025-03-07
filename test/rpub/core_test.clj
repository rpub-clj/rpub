(ns rpub.core-test
  (:require [clojure.test :refer [deftest is]]
            [rpub.core :as rpub]
            [rpub.lib.permalinks :as permalinks]
            [rpub.test-util]))

(deftest url-for-test
  (let [req {:permalink-router (permalinks/default-permalink-router)
             :site-base-url "http://localhost:3000"}
        content-item {:content-type {:slug "posts"}
                      :fields {"Slug" "my-post"}}]
    (is (= "http://localhost:3000/posts/my-post"
           (rpub/url-for content-item req)))))

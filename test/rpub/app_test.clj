(ns rpub.app-test
  (:require [clojure.test :refer [deftest]]
            [etaoin.api :as e]
            [rpub.test-util :as tu]))

(deftest ^:integration app-test
  (tu/with-rpub-server
    (fn [{:keys [d base-url]}]
      (let [view-site-button "[data-test-id='view-site-button']"]
        (e/wait-visible d view-site-button)
        (let [site-path (e/get-element-attr d view-site-button :href)
              site-url (str base-url site-path)]
          (e/go d site-url)
          (tu/wait-url d site-url)
          (e/wait-predicate
            (fn []
              (= (:site-title tu/setup-fields) (e/get-title d)))))))))

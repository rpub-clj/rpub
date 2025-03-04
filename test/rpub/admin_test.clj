(ns rpub.admin-test
  (:require [clojure.test :refer [deftest]]
            [etaoin.api :as e]
            [rpub.test-util :as tu]))

(deftest ^:integration dashboard-test
  (tu/with-rpub-server
    (fn [{:keys [d]}]
      (doto d
        (e/wait-visible "[data-test-id='dashboard-content-types']")
        (tu/wait-match-text
          "[data-test-id='dashboard-theme']"
          #"(?s).*This site is using the Starter Theme.*")
        (tu/wait-match-text
          "[data-test-id='dashboard-plugins']"
          #"(?s).*This site has 2 plugins activated.*")
        (tu/wait-match-text
          "[data-test-id='dashboard-plugins']"
          #"(?s).*Starter Theme.*")
        (tu/wait-match-text
          "[data-test-id='dashboard-plugins']"
          #"(?s).*External Editing.*")
        (tu/wait-match-text
          "[data-test-id='dashboard-settings']"
          #"(?s).*Permalinks: /:content-type-slug/:content-item-slug.*")
        (e/wait-visible "[data-test-id='dashboard-user']")
        (e/wait-visible "[data-test-id='dashboard-server']")))))

(deftest ^:integration users-test
  (tu/with-rpub-server
    (fn [{:keys [d base-url]}]
      (e/click-visible d "[data-test-id='menu-item-users']")
      (tu/wait-url d (format "%s/admin/users" base-url)))))

(deftest ^:integration settings-test
  (tu/with-rpub-server
    (fn [{:keys [d base-url]}]
      (e/click-visible d "[data-test-id='menu-item-settings']")
      (tu/wait-url d (format "%s/admin/settings" base-url))
      (e/wait-predicate
        (fn []
          (= (:site-title tu/setup-fields)
             (e/get-element-value d "input[name='site-title']")))))))

(deftest ^:integration themes-test
  (tu/with-rpub-server
    (fn [{:keys [d base-url]}]
      (e/click-visible d "[data-test-id='menu-item-themes']")
      (tu/wait-url d (format "%s/admin/themes" base-url)))))

(deftest ^:integration plugins-test
  (tu/with-rpub-server
    (fn [{:keys [d base-url]}]
      (e/click-visible d "[data-test-id='menu-item-plugins']")
      (tu/wait-url d (format "%s/admin/plugins" base-url)))))

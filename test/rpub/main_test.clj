(ns rpub.main-test
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.string :as str]
            [clojure.test :refer [deftest]]
            [etaoin.api :as e]
            [rpub.test-util :as tu]))

(defn docker-compose-up! [{:keys [start-opts]}]
  (let [{:keys [::tu/test-dir ::tu/test-instance-id port]} start-opts
        config-path (fs/path test-dir "docker-compose.yaml")
        container-name (format "rpub-test-%s:" test-instance-id)
        container-ports (format "%d:3000" port)
        config-str (-> (slurp "docker-compose.yaml")
                       (str/replace #"^  rpub:$" (str "  " container-name ":"))
                       (str/replace "3000:3000" container-ports))]
    (spit (str config-path) config-str)
    (p/shell {:dir test-dir} "docker compose up -d")
    (p/process {:dir test-dir :out :inherit :err :inherit} "docker compose logs -f")))

(defn docker-compose-down! [{:keys [start-opts]}]
  (let [{:keys [::tu/test-dir]} start-opts]
    (p/shell {:dir test-dir} "docker compose down -v")))

(deftest ^:docker docker-compose-test
  (tu/with-rpub-server
    {::tu/start-fn docker-compose-up!
     ::tu/stop-fn docker-compose-down!}
    (fn [{:keys [d]}]
      (e/wait-visible d "[data-test-id='dashboard-content-types']"))))

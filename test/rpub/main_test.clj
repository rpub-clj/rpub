(ns rpub.main-test
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [clj-yaml.core :as yaml]
            [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.test :refer [deftest]]
            [etaoin.api :as e]
            [rpub.test-util :as tu]))

(def ^:private deps-edn (delay (edn/read-string (slurp "deps.edn"))))
(def ^:private version (delay (get-in @deps-edn [:aliases :neil :project :version])))

(defn update-docker-compose-config [config port test-instance-id]
  (-> config
      (assoc-in [:services :rpub :image] (format "rpub/rpub:%s" @version))
      (assoc-in [:services :rpub :ports] [(format "%d:3000" port)])
      (update :services
              set/rename-keys
              {:rpub (keyword (format "rpub-test-%s" test-instance-id))})))

(defn docker-compose-up! [{:keys [start-opts]}]
  (let [{:keys [::tu/test-dir ::tu/test-instance-id port]} start-opts]
    (spit (str (fs/path test-dir "docker-compose.yaml"))
          (-> (yaml/parse-string (slurp "docker-compose.yaml"))
              (update-docker-compose-config port test-instance-id)
              yaml/generate-string))
    (p/shell {:dir test-dir} "docker compose up -d")
    (p/process {:dir test-dir :out :inherit :err :inherit}
               "docker compose logs -f")))

(defn docker-compose-down! [{:keys [start-opts]}]
  (let [{:keys [::tu/test-dir]} start-opts]
    (p/shell {:dir test-dir} "docker compose down -v")))

(deftest ^:docker docker-compose-test
  (tu/with-rpub-server
    {::tu/start-fn docker-compose-up!
     ::tu/stop-fn docker-compose-down!}
    (fn [{:keys [d]}]
      (e/wait-visible d "[data-test-id='dashboard-content-types']"))))

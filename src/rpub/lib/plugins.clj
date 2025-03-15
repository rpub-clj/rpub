(ns rpub.lib.plugins
  {:no-doc true}
  (:require [babashka.http-client :as http]
            [babashka.json :as json]
            [clojure.edn :as edn]
            [medley.core :as medley]
            [rads.inflections :as inflections]
            [rpub.lib.deps :as deps])
  (:import (java.time Duration Instant)))

(def ^:dynamic *defaults*
  {:index-url "https://plugins.rpub.dev/index.json"})

(defonce cache (atom nil))

(def cache-max-age-minutes 5)

(defn- installed? [plugin]
  (let [deps-edn (edn/read-string (slurp (deps/deps-path)))]
    (contains? (:deps deps-edn) (deps/->plugin-lib-sym plugin))))

(defn install! [plugin _]
  (when-not (installed? plugin)
    (deps/add-deps plugin)
    (deps/add-require plugin)))

(defn remote-plugin? [remote-plugins plugin]
  (contains? (set (map :key remote-plugins)) (:key plugin)))

(defn- current-plugin? [current-plugins plugin]
  (contains? (set (map :key current-plugins)) (:key plugin)))

(defn can-activate? [current-plugins repo-plugins plugin]
  (or (remote-plugin? repo-plugins plugin)
      (current-plugin? current-plugins plugin)))

(defn- cache-hit? [{:keys [timestamp]}]
  (when timestamp
    (let [now (Instant/now)
          age-minutes (.toMinutes (Duration/between timestamp now))]
      (<= age-minutes cache-max-age-minutes))))

(defn- read-json [s]
  (json/read-str s {:key-fn #(keyword (inflections/hyphenate %))}))

(defn- json->plugin [json]
  (update json :key edn/read-string))

(defn- update-cache! []
  (let [http-opts {:headers {"Accept-Encoding" ["gzip" "deflate"]}}
        res (http/get (:index-url *defaults*) http-opts)
        index (-> (read-json (:body res))
                  (update :plugins #(map json->plugin %)))]
    (reset! cache {:timestamp (Instant/now), :index index})))

(defn get-plugins []
  (let [v @cache
        {:keys [index]} (if (cache-hit? v) v (update-cache!))]
    (:plugins index)))

(defn get-latest-sha []
  (let [v @cache
        {:keys [index]} (if (cache-hit? v) v (update-cache!))]
    (:sha index)))

(defn uninstall! [plugin]
  (deps/remove-require plugin)
  (deps/remove-deps plugin))

(defn- expand-plugin-middleware [plugins opts]
  (->> plugins
       (mapcat (fn [{:keys [middleware] :as plugin}]
                 (when middleware
                   (map (fn [wrap] [plugin wrap]) (middleware opts)))))))

(defn- plugin-activated? [plugins plugin]
  (->> plugins
       (medley/find-first #(= (:key %) (:key plugin)))
       :activated))

(defn- only-use-activated-middleware [expanded-middleware]
  (->> expanded-middleware
       (map (fn [[plugin wrap]]
              (fn [handler-without-plugin]
                (let [handler-with-plugin (wrap handler-without-plugin)]
                  (fn [{:keys [plugins] :as req}]
                    (if (plugin-activated? plugins plugin)
                      (handler-with-plugin req)
                      (handler-without-plugin req)))))))))

(defn plugin-middleware [{:keys [plugins] :as opts}]
  (-> plugins
      (expand-plugin-middleware opts)
      only-use-activated-middleware))

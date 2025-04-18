(ns rpub.test-util
  (:require [babashka.fs :as fs]
            [etaoin.api :as e]
            [rpub.lib.malli :as malli]
            [rpub.main :as main]
            [rpub.plugins.external-editing]
            [rpub.plugins.starter-theme]
            [taoensso.telemere :as tel])
  (:import (java.net ServerSocket)))

(malli/start-dev!)

(def ^:dynamic *etaoin-opts*
  {:type :firefox
   :headless true})

(defn- with-driver [f]
  (let [{:keys [type]} *etaoin-opts*
        opts (dissoc *etaoin-opts* :type)]
    (e/with-driver type opts driver
      (let [driver' (e/use-css driver)]
        (f driver')))))

(defn- random-port []
  (with-open [socket (ServerSocket. 0)]
    (.getLocalPort socket)))

(defn- default-start-opts [{:keys [test-dir]}]
  (let [data-dir (fs/path test-dir "data")]
    {::start-fn (fn [{:keys [start-opts]}] (main/start! start-opts))
     ::stop-fn (fn [_] (main/stop!))
     :banner true
     :secret-key-file (format "%s/test.key" data-dir)
     :content-security-policy false
     :database-url (format "jdbc:sqlite:%s/test.db" data-dir)
     :db-type :sqlite
     :error-page false
     :port (random-port)
     :reload false
     :clj-repl false}))

(defn wait-url [driver url]
  (e/wait-predicate
    (fn []
      (tel/log! :info (format "matching url: %s" url))
      (= (e/get-url driver) url))))

(defn wait-match-text [driver q re]
  (e/wait-predicate
    (fn []
      (let [text (e/get-element-text driver q)]
        (tel/log! :info (format "matching element text: %s" text))
        (re-matches re text)))))

(def setup-fields
  {:username "test-user"
   :password "test-password"
   :site-title "Test Site"})

(defn- default-retry-timeline []
  (->> (iterate #(+ 5000 %) 0) (take 10)))

(defn- with-retries [f & {:keys [timeline]
                          :or {timeline (default-retry-timeline)}}]
  (loop [[t1 t2 :as remaining] timeline]
    (let [result (try (f) (catch Throwable e e))]
      (when (instance? Throwable result)
        (if t2
          (do
            (let [delay-ms (- t2 t1)]
              (tel/log! :info (format "Retrying after %sms..." delay-ms))
              (Thread/sleep ^long delay-ms)
              (recur (rest remaining))))
          (throw result))))))

(defn- setup [driver base-url]
  (let [d driver
        {:keys [username password site-title]} setup-fields]
    (with-retries #(e/go d base-url))
    (wait-url d (format "%s/admin/setup" base-url))
    (e/wait-visible d "#username")
    (e/fill d "#username" username)
    (e/fill d "#password" password)
    (e/fill d "#site-title" site-title)
    (e/click d "button[type=submit]")
    (wait-url d (format "%s/admin" base-url))))

(defn with-rpub-server
  ([f] (with-rpub-server nil f))
  ([start-opts f]
   (with-driver
     (fn [driver]
       (let [test-dir (fs/delete-on-exit (fs/create-temp-dir))
             defaults (default-start-opts {:test-dir test-dir})
             test-instance-id (random-uuid)
             read-only-start-opts {::test-dir test-dir
                                   ::test-instance-id test-instance-id}
             start-opts' (merge defaults start-opts read-only-start-opts)
             {:keys [::start-fn ::stop-fn port]} start-opts'
             base-url (format "http://localhost:%s" port)
             server (start-fn {:start-opts start-opts'})]
         (try
           (setup driver base-url)
           (f {:start-opts start-opts'
               :server server
               :base-url base-url
               :d driver})
           (finally
             (stop-fn {:start-opts start-opts'}))))))))

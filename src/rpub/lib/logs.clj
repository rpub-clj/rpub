(ns rpub.lib.logs
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [taoensso.telemere :as tel]
            [taoensso.telemere.utils :as tel-utils])
  (:import (java.io OutputStreamWriter PrintWriter Writer)))

(defn bling-signal-fn []
  (let [clean-signal (tel-utils/clean-signal-fn)]
    (fn [signal]
      (let [bling (requiring-resolve 'bling.core/bling)
            callout (requiring-resolve 'bling.core/callout)
            czprint-str (requiring-resolve 'zprint.core/czprint-str)
            {:keys [level kind id data msg_] :as signal'} (clean-signal signal)
            label-str (bling [:bold (str (str/upper-case (name level))
                                         " - "
                                         (:ns signal')
                                         (when (= kind :event)
                                           (str " - " id)))])
            msg-str (if (= kind :event) (czprint-str data) msg_)]
        (callout {:type level, :label label-str, :theme :gutter}
                 msg-str)))))

(defn console-handler [{:keys [output-fn]}]
  (let [system-err (PrintWriter. System/err)
        system-out (OutputStreamWriter. System/out)]
    (fn
      ([]) ; Stop => noop
      ([signal]
       (let [^Writer stream (if (tel-utils/error-signal? signal)
                              system-err
                              system-out)]
         (when-let [output (output-fn signal)]
           (.write stream (str output))
           (.flush stream)))))))

(defn setup! [{:keys [logs-pretty logs-path]}]
  (run! tel/remove-handler! (keys (tel/get-handlers)))
  (tel/add-handler!
    ::file
    (tel/handler:file
      {:path (str (fs/path logs-path "app.edn"))
       :output-fn (tel-utils/pr-signal-fn)}))
  (tel/add-handler!
    ::console
    (console-handler
      {:output-fn (if logs-pretty
                    (bling-signal-fn)
                    (tel-utils/format-signal-fn))})))

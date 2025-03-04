(ns rpub.lib.secrets
  {:no-doc true}
  (:require [buddy.core.codecs :as codecs]
            [buddy.core.crypto :as crypto]
            [buddy.core.nonce :as nonce]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]))

(def ^:private buddy-opts
  {:alg :aes128-cbc-hmac-sha256})

(defrecord Secret [s])

(defn ->secret [s]
  (Secret. s))

(defmethod print-method Secret [record writer]
  (.write writer (str "#rpub/secret " (pr-str (:s record)))))

(defmethod pprint/simple-dispatch Secret [secret]
  (pr secret))

(defn encrypt [secrets-data secret-key]
  (let [iv (nonce/random-bytes 16)
        secrets-bytes (codecs/str->bytes (pr-str secrets-data))
        encrypted (crypto/encrypt secrets-bytes secret-key iv buddy-opts)
        secrets-map {:iv (codecs/bytes->b64-str iv)
                     :encrypted (codecs/bytes->b64-str encrypted)}
        secret-str (-> (pr-str secrets-map)
                       codecs/str->bytes
                       codecs/bytes->b64-str)]
    (->secret secret-str)))

(defn- b64-str->bytes [s]
  (-> s codecs/str->bytes codecs/b64->bytes))

(defn decrypt [secret-record secret-key]
  (let [{:keys [iv encrypted]} (some-> (:s secret-record)
                                       b64-str->bytes
                                       codecs/bytes->str
                                       edn/read-string
                                       (update-vals b64-str->bytes))]
    (-> (crypto/decrypt encrypted secret-key iv buddy-opts)
        codecs/bytes->str
        edn/read-string)))

(defn get-secret-key [secret-key-file]
  (let [f (io/file secret-key-file)]
    (when (.exists f)
      (codecs/hex->bytes (slurp f)))))

(defn init-secret-key [secret-key-file]
  (or (get-secret-key secret-key-file)
      (let [secret-key (nonce/random-bytes 32)
            f (io/file secret-key-file)]
        (io/make-parents f)
        (spit f (codecs/bytes->hex secret-key))
        secret-key)))

(comment
  (def sk (init-secret-key (clojure.java.io/file "config" "secret.key")))
  (count sk)
  (count (nonce/random-bytes 32))
  (-> (encrypt {:a 1} sk) (decrypt sk)))

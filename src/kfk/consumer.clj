(ns kfk.consumer
  (:import [java.security MessageDigest]
           [java.math.BigInteger])
  (:require [kinsky.client         :as client]
            [kinsky.async          :as async]
            [taoensso.timbre       :as log]
            [kfk.worker            :as w]
            [buddy.core.codecs     :as codecs]
            [buddy.core.bytes      :as bytes]
            [clojure.core.async :as a :refer [go <! >!]]))

(defn md5
  [^String s]
  (->> s
       .getBytes
       (.digest (MessageDigest/getInstance "MD5"))
       (BigInteger. 1)
       (format "%032x")))

(defn init []
  (let [username "a4e21756ae15d90c939537e9c2b4f160"
        password "9254604effc3d26a74d1629f3ea1e59e"
        servers  "121.199.70.37:9092"
        key      (codecs/hex->bytes password)
        authjar  "org.apache.kafka.common.security.plain.PlainLoginModule"
        topic    (str "device-data-" username)
        config   (->> password
                     md5
                     (str username)
                     md5
                     (format "%s required username=\"%s\" password=\"%s\";" authjar username))
        [events-ch control-ch] (async/consumer {:bootstrap.servers servers
                                                :group.id username
                                                :security.protocol "SASL_PLAINTEXT"
                                                :sasl.mechanism "PLAIN"
                                                :sasl.jaas.config config}
                                               (client/string-deserializer)
                                               (client/string-deserializer))
        topic topic]

    ;; start workers
    (do (w/start-a-consumers 8 key)
        (w/start-a-aggregator))

    (a/go-loop []
      (when-let [record (a/<! events-ch)]
        ;(log/debug "\n----------------------------------------- kafkaMsg ---------------------------------------------------\n" record)
        (if (contains? record :value)
          (a/>!! w/in-chan (:value record))
          (log/debug "meta info of kafka >>> " record))
        (recur)))
    (a/put! control-ch {:op :subscribe :topic topic})
    (a/put! control-ch {:op :commit})))




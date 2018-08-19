(ns kfk.worker
  (:require [taoensso.timbre    :as log]
            [cheshire.core      :as json]
            [base64-clj.core    :as base64]
            [buddy.core.crypto  :as crypto]
            [buddy.core.bytes   :as bytes]
            [kfk.parser         :as parser]
            [clojure.core.async :as a]))

(def in-chan (a/chan))
(def out-chan (a/chan))

(defn process
  "Do 'work'"
  [value key]
  (let [pld (json/parse-string value true)
        code (:code pld)
        body (base64/decode-bytes (.getBytes (:body pld)))
        iv16 (bytes/slice body 0 16) 
        cypher (bytes/slice body 16 (count body))
        plaintext (slurp (crypto/decrypt cypher key iv16 {:algorithm :aes128--cbc-hmac-sha256}))]
    ;(log/debug "\n--------------------------------------- plaintext ---------------------------------------------------\n" plaintext)

    ;; TODO: process the plaintext, should use defmulti
    (let [data (json/parse-string plaintext true)]
      (log/debug "\n------------------------------------- JsonText ----------------------------------------------------\n" data)
      (parser/parse code data))))

(defn start-a-consumers
  "Start num-consumers threads that will consume work
  from the in-chan and put it into the out-chan."
  [num-consumers key]
  (dotimes [i num-consumers]
    (log/debug "starting worker-" i)
    (a/thread
      (while true
        (let [value (a/<!! in-chan)
              data (process value key)]
          (a/>!! out-chan key))))))

(defn start-a-aggregator
  "Take items from the out-chan and print it."
  []
  (log/debug "starting aggregator =>")
  (a/thread
    (while true
      (let [data (a/<!! out-chan)]
        (println data)))))

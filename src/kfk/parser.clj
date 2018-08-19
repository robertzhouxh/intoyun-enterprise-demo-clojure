(ns kfk.parser
  (:require [taoensso.timbre    :as log]
            [cheshire.core      :as json]
            [base64-clj.core    :as base64]))

(defn disparch [code pld]
  (cond
    (= code 10) :online
    (= code 11) :wifi-gprs-meta
    (= code 12) :lora-gate-meta
    (= code 13) :lora-node-meta
    (= code 14) :tcp-ws-meta
    (= code 21) :wifi-grps-rx
    (= code 22) :lora-gate-rx
    (= code 23) :lora-node-rx
    (= code 24) :tcp-ws-rx
    :else :unknown-code))

(defmulti parse disparch)

;; TODO:(robertzhouxh@gmail.com) custom your own bussines logic
;; 1. persistence the data to the mysql, mongodb, redis, kafka ... 
;; 2. push the data to the websocket client of the user
;; 3. analyze the data

(defmethod parse :online [code pld]
  (let [devid (:devId pld)
        data (base64/decode (:data pld))]
    (let [fields (json/parse-string data true)
          online (:online fields)]
      (log/debug "WIFI-Device [" devid "] Online: " online))))

(defmethod parse :wifi-gprs-meta [code pld]
  (let [devid (:devId pld)
        data (base64/decode (:data pld))]
    (let [fields (json/parse-string data true)]
      (log/debug "WIFI-Device [" devid "] metainfo: " fields))))

(defmethod parse :lora-gate-meta [code pld]
  (let [devid (:devId pld)
        data (base64/decode (:data pld))]
    (let [fields (json/parse-string data true)]
      (log/debug "LORA-GATE-Device [" devid "] metainfo: " fields))))

(defmethod parse :lora-node-meta [code pld]
  (let [devid (:devId pld)
        data (base64/decode (:data pld))]
    (let [fields (json/parse-string data true)]
      (log/debug "LORA-NODE-Device [" devid "] metainfo: " fields))))

(defmethod parse :tcp-ws-meta [code pld]
  (let [devid (:devId pld)
        data (base64/decode (:data pld))]
    (let [fields (json/parse-string data true)]
      (log/debug "TCP-WS-Device [" devid "] metainfo: " fields))))

(defmethod parse :wifi-grps-rx [code pld] (log/debug "TODO: wifi-grps-rx ===> " (base64/decode (:data pld))))
(defmethod parse :lora-gate-rx [code pld] (log/debug "TODO: lora-gate-rx ===> " (base64/decode (:data pld))))
(defmethod parse :lora-node-rx [code pld] (log/debug "TODO: lora-node-rx ===> " (base64/decode (:data pld))))
(defmethod parse :tcp-ws-rx [code pld]    (log/debug "TODO: tcp-ws-rx ===> " (base64/decode (:data pld))))
(defmethod parse :unknown-code [code pld] (log/error "TODO: invalid code with pld:===> " pld))



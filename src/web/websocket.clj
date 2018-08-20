(ns web.websocket
  (:require
   [taoensso.timbre    :as log]
   [compojure.core :as compojure :refer [GET]]
   [ring.middleware.params :as params]
   [compojure.route :as route]
   [aleph.http :as http]
   [byte-streams :as bs]
   [manifold.stream :as s]
   [manifold.deferred :as d]
   [manifold.bus :as bus]
   [base64-clj.core    :as base64]
   [cheshire.core      :as json]
   [clojure.core.async :as a]))

(def evbus (bus/event-bus))

(def uid "uid-admin")

(def non-websocket-request
  {:status 400
   :headers {"content-type" "application/text"}
   :body "Expected a websocket request."})

(defn publish
  ""
  [msg key]
  (log/debug "publish to key: " key "===> msg: " msg)
  (bus/publish! evbus key (json/generate-string msg)))

(defn pipe-parse-1
  "parse the input message first"
  [msg]
  (let [data (json/parse-string msg true)
        op (:op data)
        body (:body data)]
    (log/debug "RECV msg ===>" msg)
    (case op 
      4 (do
          (log/debug "Heartbeat <---")
          (json/generate-string {:op 5 :body body}))
      "default" (log/debug "unknown op code"))))

(defn ws-handler 
  [req]
  (d/let-flow [conn (d/catch
                      (http/websocket-connection req)
                      (fn [_] nil))]
    (if-not conn
      non-websocket-request
      ;; take the first auth message
      (d/let-flow [sink (s/stream)
                   msg (s/take! conn)
                   auth (json/parse-string msg true)
                   op (:op auth)
                   body (base64/decode (:body auth))
                   key (:key (json/parse-string body true))]
        (case (:op auth)
          2 (do
              (s/put! conn (json/generate-string {:op 3 :body (base64/encode (json/generate-string {:key key}))}))
              (log/debug "auth successfully for key ===>" key) 
              ;; take all messages from the evbus, and feed them to the conn
              (s/connect
               (bus/subscribe evbus key)
               conn)
              ;; take all messages from the client and publish to evbus
              (s/consume
               #(bus/publish! evbus key %)
               (->> conn
                    ;;(s/consume #(prn 'message! %))
                    ;;(s/connect-via a #(s/put! b (inc %)) b)
                    ;;(s/consume pipe-parse-1)
                    ;;(s/map #(str %))
                    (s/map #(pipe-parse-1 %))
                    (s/buffer 100))))
          "default" (log/debug "Auth failed for: invalid op"))
        ))))

(def handler
  (params/wrap-params
   (compojure/routes
      (GET "/sub" [] ws-handler)
      (route/not-found "No such page."))))
(defn start
  ""
  [port]
  (http/start-server handler {:port port}))


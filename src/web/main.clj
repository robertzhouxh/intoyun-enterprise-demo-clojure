(ns web.main
  (:require
    [ring.adapter.jetty :as jetty]
    [ring.middleware.reload :refer [wrap-reload]]
    [ring.middleware.cookies :as cookies]
    [ring.middleware.params :as params]
    [ring.middleware.keyword-params :as keyword-params]
    [ring.middleware.json :as json]
    [ring.middleware.resource :as resource]
    [ring.middleware.stacktrace :as stacktrace]
    [kfk.consumer :as consumer]
    [web.websocket :as websocket]
    [web.handler :as handler])
  (:gen-class))

(def app
  (->
   handler/routes
   (resource/wrap-resource (clojure.java.io/resource "resources/"))
   json/wrap-json-response
   ;json/wrap-json-body
   (json/wrap-json-body {:keywords? true})
   ;;stacktrace/wrap-stacktrace-web
   keyword-params/wrap-keyword-params
   cookies/wrap-cookies
   params/wrap-params))

(defn -main
  [http-port]
  (consumer/init)
  (websocket/start 9090)
  (jetty/run-jetty #'app {:port (Integer. http-port)}))
(defn -dev-main
  "Reloads server when changes are made to code, including -main adapter"
  [http-port]
  (consumer/init)
  (websocket/start 9090)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. http-port)}))

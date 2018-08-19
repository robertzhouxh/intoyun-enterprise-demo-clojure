(ns web.handler
  (:require [jdbc.user :as user]
            [taoensso.timbre :as log])
  (:use [compojure.core :only [GET POST PUT DELETE defroutes context]]
        [compojure.route :only [not-found]]
        [ring.util.response :only [response redirect]]))

;; (defn uuid [] (str (java.util.UUID/randomUUID)))

(defn register
  [body]
  (let [id (str (System/currentTimeMillis))
        uname (:uname body)
        password (:password body)]
    (log/debug "register for uname: " uname)
    (user/create-account {:id id
                        :uname uname
                        :password password})
    (response {:id id})))

(defn login
  ([uname password]
   (response "login successfully"))
  ([req]
   (response "login successfully")))

(defroutes routes
  (GET "/" request
       (redirect "/index.html"))
  (context "/accounts" []
           (POST "/register" {body :body} (register body))
           (GET "/login" [uname, password] (login uname password))
           (GET "/login" {{uname :uname, password :password} :params} (login uname password))
           (GET "/login" request (login request)))
  (POST "/" [](fn [request]
                (let [params (:params request)]
                  (redirect "/"))))
  (not-found "<h1>page not found!</h1>"))


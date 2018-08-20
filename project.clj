(defproject web "1.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 ;[org.clojure/java.jdbc "0.7.8"]
                 [com.taoensso/timbre "4.10.0"]           ;; log lib
                 [ring "1.7.0-RC1"]                       ;; http lib
                 [ring/ring-json "0.4.0"]                 ;; middleware
                 [compojure "1.6.1"]                      ;; router
                 [spootnik/kinsky "0.1.22"]               ;; kafka lib
                 ;[mysql/mysql-connector-java "8.0.12"]   ;; mysql driver 
                 [mysql/mysql-connector-java "5.1.38"]    ;; mysql driver: new driver class is `com.mysql.cj.jdbc.Driver since 6.*.*
                 [korma "0.4.3"]                          ;; mysql wrap
                 [cheshire "5.8.0"]                       ;; json
                 [base64-clj "0.1.1"]                     ;; base64
                 [robertzhouxh/buddy-core "1.5.1"]        ;; crypto(aes)
                 [funcool/cuerdas "2.0.5"]                ;; string <http://funcool.github.io/cuerdas/latest/>
                 [aleph "0.4.6"]                          ;; websocket/tcp/udp
                 ;; ;; fix bug for SLF4J >>>>>> Failed to load class ...
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :plugins [[clj-sql-up "0.4.1"]]
  :clj-sql-up {:database {:subprotocol "mysql"
                          :subname "//127.0.0.1:3306/mydb"
                          :user "molmc"
                          :password "123456"}
               :deps [[mysql/mysql-connector-java "5.1.29"]]}
  :ring {:handler main/app
         :auto-reload? true
         :auto-refresh? true}
  :main web.main
  :aot [web.main])

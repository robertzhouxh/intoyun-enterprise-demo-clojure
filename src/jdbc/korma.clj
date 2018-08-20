(ns jdbc.korma
  (:use korma.db
        korma.core))

(defdb korma-db
  (mysql {:db "mydb"
          :host "127.0.0.1"
          :port 3306
          :user "molmc"
          :password "123456"}))

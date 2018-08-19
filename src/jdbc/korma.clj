(ns jdbc.korma
  (:use korma.db
        korma.core))

(defdb korma-db
  (mysql {:db "dbridge"
          :host "127.0.0.1"
          :port 3306
          :user "dbridge"
          :password "123456"}))

(ns jdbc.user
  (:use korma.db
        korma.core
        jdbc.korma))

;; define the talbe name
(declare accounts)
(defentity accounts)

;; ------------------- accounts -----------------
(defn create-account [account]
  (insert accounts
          (values account)))

(defn get-account[id]
  (select accounts
          (where {:id id})))


;; migrations/20180816102714302-accounts.clj

(defn up []
  ["CREATE TABLE foo(id int)"
   "CREATE INDEX foo_index ON foo(id)"
   "CREATE TABLE info (id varchar(15) NOT NULL, info_key varchar(255) NOT NULL, info_value varchar(255) NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"
   "CREATE TABLE accounts (id varchar(128) NOT NULL, uname varchar(255) NOT NULL, password varchar(255) NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"
   ])

(defn down []
  ["drop table foo"
   "drop table info"
   "drop table accounts"
   ])

(ns twitter-stuff.couch
  (:require [com.ashafa.clutch :as clutch]
            [cemerick.url :refer [url]]
            [environ.core :refer [env]]))


(def db (clutch/get-database (assoc (url "http://24.186.113.22:5984/twitter")
                               :username "admin"
                               :password (env :couchdb-admin-pword))))


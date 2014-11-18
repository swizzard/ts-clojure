(ns twitter-stuff.utils.couch
  (:require [com.ashafa.clutch :as clutch]
            [cemerick.url :refer [url]]
            [environ.core :refer [env]]
            [clojure.core.match :refer [match]]))


(def db (clutch/get-database "http://24.186.113.22:5984/twitter"))

(defn multi-update [old new]
  (if (nil? old)
    new
    (match [(sequential? old) (sequential? new)]
           [true true] (into old new)
           [true false] (conj old new)
           [false true] (into (vector old) new)
           [false false] [old new])))

(defn conj-doc [db id k addition]
    (if-let [updated
             (update-in (clutch/get-document db id)
                        [k] multi-update addition)]
        (clutch/put-document db (assoc updated :_id id))
        (clutch/put-document db (assoc {:_id id} k addition))))

(defn assoc-doc [db id m]
    (clutch/put-document db
     (merge (clutch/get-document db id) m)))

(defn hashtags-to-db [db hashtags]
      (doseq [hashtag hashtags] 
   	 (conj-doc db (:hashtag hashtag) :tweets (:tweet hashtag))))

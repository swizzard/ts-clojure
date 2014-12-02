(ns twitter-stuff.utils.couch
  (:require [com.ashafa.clutch :as clutch]
            [cemerick.url]
            [environ.core :refer [env]]))


(defn get-db [] (clutch/get-database (assoc (cemerick.url/url "http://127.0.0.1/"
						 "twitter")
					:port 5984
					:username "swizzard"
					:password "discworld")))

(defn multi-update [old new]
  (if (nil? old)
    [new]
    (if (sequential? new)
      (into old new)
      (conj old new))))

(defn assoc-doc [db id m]
    (clutch/put-document db
     (merge (clutch/get-document db id) m)))

(defn hashtags-to-db [db hashtags]
      (doseq [hashtag hashtags] 
        (let [doc (or (clutch/get-document db (:hashtag hashtag)) {:_id (:hashtag hashtag) :tweets []})]
          (clutch/put-document db (update-in doc [:tweets] conj (:tweet hashtag))))))


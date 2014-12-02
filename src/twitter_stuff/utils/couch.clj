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
        (let [doc (or (clutch/get-document db hashtag) {:_id (:hashtag hashtag) :tweets []})]
          (update-in doc [:tweets] conj (:tweet hashtag)))))


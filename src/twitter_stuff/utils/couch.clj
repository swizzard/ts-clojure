(ns twitter-stuff.utils.couch
  (:require [com.ashafa.clutch :as clutch]
            [cemerick.url]
            [environ.core :refer [env]]))

(defn get-db [] (clutch/get-database (assoc (cemerick.url/url "http://127.0.0.1/"
						 "twitter")
					:port 5984
					:username "swizzard"
					:password (env :couchdb-admin-pword))))

(defn multi-update [old new]
  (if (nil? old)
    [new]
    (if (sequential? new)
      (into old new)
      (conj old new))))

(defn assoc-doc [db id m]
    (clutch/put-document db
     (merge (clutch/get-document db id) m)))

(defn hashtag-to-db [db hashtag]
      (let [doc (or (clutch/get-document db (:hashtag hashtag)) {:_id (:hashtag hashtag) :tweets []})]
        (clutch/put-document db (update-in doc [:tweets] conj (:tweet hashtag)))))

(defn hashtags-to-db [db hashtags]
      (doseq [hashtag hashtags] 
        (hashtag-to-db db hashtag)))

(defn get-all-docs [db & {:keys [with-docs]}]
	(clutch/all-documents db {:include_documents (or with-docs false)}))

(defn count-tweets [] (reduce + (map #(-> % (get-in [:doc :tweets]) count) (clutch/all-documents (get-db) {:include_docs true}))))

(defn co-ocs [db ht & [s]] 
	(reduce into (or s #{}) 
		(map #(map :text %) 
	      	      (map #(get-in % [:entities :hashtags]) 
	      	    	    (:tweets (clutch/get-document db ht))))))

(defn co-occurrences [db ht depth]
	(loop [tags (co-ocs db ht) i 1] 
		(if (<= i depth) (recur 
				(reduce into tags 
					(map (partial co-ocs db) tags)) 
			    	(inc i)) 
		tags)))
	

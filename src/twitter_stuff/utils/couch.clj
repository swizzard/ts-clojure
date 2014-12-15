(ns twitter-stuff.utils.couch
  (:require [com.ashafa.clutch :as clutch]
            [cemerick.url]
            [environ.core :refer [env]]))

(defn get-db [& [db-url db-name]] (clutch/get-database (assoc (cemerick.url/url (or db-url "http://127.0.0.1")
						 (or db-name "twitter"))
					:port (env :couchdb-port)
					:username (env :couchdb-username)
					:password (env :couchdb-admin-pword)
                    :socket-timeout 10000
                    :conn-timeout 10000)))

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


(defn get-all-docs [db & {:keys [with-docs]}]
	(clutch/all-documents db {:include_docs (or with-docs false)}))

(defn count-tweets [& [db-url db-name]] (reduce + (map #(-> % (get-in [:doc :tweets]) count) (clutch/all-documents (get-db (or db-url "127.0.0.1")
                                                                                                                           (or db-name "twitter")) 
                                                                                                                   {:include_docs true}))))

(defn extract-tags [doc] (let [tweets (get-in doc [:doc :tweets])]
                           (map (fn [t] (map :text #(map (get-in % [:entities :hashtags]) t))) tweets)))


(defn co-ocs [ht & [s]] 
	(reduce into (or s []) 
		(map #(map :text %) 
	      	      (map #(get-in % [:entities :hashtags]) 
	      	    	    (get-in ht [:doc :tweets])))))

(defn co-occurrences [ht depth]
	(loop [tags (co-ocs ht) i 1] 
		(if (<= i depth) (recur 
				(reduce into tags 
					(map (partial co-ocs) tags)) 
			    	(inc i)) 
		tags)))
	

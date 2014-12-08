(ns twitter-stuff.utils.mongo
	(:require (monger [core :as mg]
			  [collection :as mc]
			  [operators :as ops])))

(def conn (mg/connect))
(def twitter-db (mg/get-db conn "twitter"))
(def coll "tweets")

(defn hashtags-to-mongo [hashtags]
	(doseq [hashtag hashtags]
		(mc/update twitter-db coll {:_id (:hashtag hashtag)}
		   {ops/$push {:tweet (:tweet hashtag)}} {:upsert true}))) 

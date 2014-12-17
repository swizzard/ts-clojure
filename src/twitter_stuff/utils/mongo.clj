(ns twitter-stuff.utils.mongo
	(:require (monger [core :as mg]
			  [collection :as mc]
			  [operators :as ops]))
	(:import org.bson.types.ObjectId))

(def conn (mg/connect))
(def twitter-db (mg/get-db conn "twitter-new"))
(def coll "tweets")

(defn tweet-to-mongo [tweet]
	(mc/insert twitter-db coll (assoc tweet :_id (ObjectId.)))) 

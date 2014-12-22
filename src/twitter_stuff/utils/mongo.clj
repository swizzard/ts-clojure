(ns twitter-stuff.utils.mongo
	(:require (monger [core :as mg]
			  [collection :as mc]
			  [operators :as ops]))
	(:import org.bson.types.ObjectId))

(def mongos-conn (mg/get-db (mg/connect {:port 27017}) "twitter-new"))

(def coll "tweets")

(defn tweet-to-mongo [conn tweet]
	(do (println "uploading")
	(mc/insert conn coll (assoc tweet :_id (ObjectId.)))))


(ns twitter-stuff.utils.tf-idf
	(:require (monger [collection :as mc]
			          [operators :as mo])
		      [twitter-stuff.utils.mongo :refer [mongos-conn
                                                 coll]]
              [twitter-stuff.utils.helpers :refer [max-by
                                                   count-all-tweets]]))

(defn count-containing-docs [t] (mc/count mongos-conn "tweets"
				                    {:hashtags t})) 

(defn get-all-tags [] (reduce into (map :hashtags (mc/find-maps 
                                                    mongos-conn "tweets"))))
(defn get-tf [t d] (let [rfs (frequencies d)
                         max-freq (second (max-by > :val rfs))
                         raw-t-f (get rfs t)]
                                (+ 0.5 (/ (* 0.5 raw-t-f)
                                          max-freq))))

(defn get-idf [t] (Math/log (/ (count-all-tweets) (count-containing-docs t))))

(defn get-tf-idf [t d] (* (get-tf t d) (get-idf t)))

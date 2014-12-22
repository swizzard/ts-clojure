(ns twitter-stuff.utils.tf-idf
	(:require (monger [collection :as mg]
			  [operators :as mo])
		  [twitter-stuff.utils.mongo [mongos-conn]]))

(def all-tweets (mg/find-maps mongos-conn "tweets"))

(def all-tags (concat (map :hashtags all-tweets)))

(defn count-containing-docs [t] (mg/count mongos-conn "tweets"
				    {:hashtags {mo/$elemMatch
						  {mo/$eq t}}}))

(defn get-adjusted-freq [t d] (let [rfs (frequencies d)
                                    max-freq (second (first
                                               (into (sorted-map-by
                                                       (fn [k1 k2]
                                                         (compare 
                                                           [(get rfs k1) k1]
                                                           [(get rfs k2) k2]))))))
                                    raw-t-f (get rfs t)]
                                (+ 0.5 (/ (* 0.5 raw-t-f)
                                          max-freq))))

(defn get-idf [t] (Math/log (/ (count terms) (count-containing-docs t))))


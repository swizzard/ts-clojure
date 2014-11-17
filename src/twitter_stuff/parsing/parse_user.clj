(ns twitter-stuff.parsing.parse-user
  (:require [twitter-stuff.parsing.parse-etc :refer [get-expanded-urls]]
   	    (clj-time format predicates)
	    [twitter-stuff.parsing.parse-etc :refer [caf]]))
(defn process-created-at [ca-date]
	(let [dobj (clj-time.format/parse caf ca-date]	

(defn process-user [m]
  (-> m
      (merge (get-expanded-urls (:url m)))
      (assoc :follow-ratio
         (/ (double (:followers_count m))
            (:friends_count m)))
      ))

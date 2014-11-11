(ns twitter-stuff.parsing.parse-user
  (:require [twitter-stuff.parsing.parse-etc :refer [get-expanded-urls]]))

(defn process-user [m]
  (-> m
      (merge (get-expanded-urls (:url m)))
      (assoc :follow-ratio
         (/ (double (:followers_count m))
            (:friends_count m)))
      ))

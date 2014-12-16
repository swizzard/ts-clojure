(ns twitter-stuff.utils.tf-idf
  (:require [twitter-stuff.utils.couch :refer [get-db
                                               co-occurrences
                                               get-all-docs]]))

(def db (get-db "http://192.168.1.2" "twitter"))
(def terms (map :key (get-all-docs db)))
(def docs (reduce (partial apply assoc) {} (map (memoize (fn [t] [t (co-occurrences db t 1)])) terms)))

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

(defn count-containing-docs [t] (count (map (fn [d] (if (some #{t} d) 1 0) docs))))

(defn get-idf [t] (Math/log (/ (count terms) (count-containing-docs t))))


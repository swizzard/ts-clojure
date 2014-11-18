(ns twitter-stuff.parsing.parse-hashtag
  (:require [instaparse.core :as insta]
            [cheshire.core :refer [parse-stream]]
            [clojure.java.io :as io]))

(def bests (atom {}))

(def word-freqs (let [m1 (parse-stream (io/reader "resources/lowered_freqs.json"))
		      m2 (parse-stream (io/reader "resources/freqs_invokeit.json"))]
		  (apply merge (map #(apply hash-map %) (map (fn [k] [k (/ (+ (get m1 k 0) (get m2 k 0)) 2)]) (distinct (concat (keys m1) (keys m2))))))))

(def oov (/ (count word-freqs)))

(def subword-parser (insta/parser
                     "<S> = W+
                     W = #'\\w'+"))
(defn parse-to-string [parse] (map #(apply str (rest %)) parse))

(defn get-subwords [s] (doseq [parses (insta/parses subword-parser s)]
                        (map (fn [p] (apply str (rest p))) parses)))


(defn rank-freqs [ss]
  (let [freqs (map #(* (get word-freqs % oov) (count %)) ss)]
  [(Math/pow (apply * freqs) (/ (count ss)))
   ss]))

(defn get-score-arith [parse-str] 
	(long (/ (apply + (map #(get word-freqs % 0) parse-str)) (count parse-str))))
(defn get-score [parse-str]
	(let [freqs (map #(* (get word-freqs % oov) (/ (count parse-str))) parse-str)]
		(Math/pow (apply * freqs) (/ (count parse-str)))))

(defn get-best-parse [s]
  (if-let [curr-best (get @bests s)]
    (first curr-best)
    (do
      (doseq [parse (insta/parses subword-parser s)]
	(let [score (get-score (parse-to-string parse))]
		(if (> score (second (get @bests s [nil 0])))
			(swap! bests assoc s [(parse-to-string parse) score]))))
      (first (get @bests s)))))


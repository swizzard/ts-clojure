(ns twitter-stuff.parsing.parse-hashtag
  (:require [instaparse.core :as insta]
            [cheshire.core :refer [parse-stream]]
            [clojure.java.io :as io]))

(def bests (atom {}))

(def word-freqs (parse-stream (io/reader "resources/freqs_combined.json")))
(def oov (long (/ (count word-freqs))))
(def subword-parser (insta/parser
                     "<S> = W+
                     W = #'\\w'+"))

(defn get-subwords [s] (map (fn [p] (apply str (rest p))) (insta/parses subword-parses s)))

(defn get-score [parse-str]
	(let [freqs (map #(* (get word-freqs % oov) 
		             (/ (count parse-str))) 
			parse-str)]
		(Math/pow (apply * freqs) (/ (count parse-str)))))

(defn get-best-parse [s]
  (if-let [curr-best (get @bests s)]
    (first curr-best)
    (do
      (doseq [parse (get-subwords s)]
	    (let [score (get-score parse)]
		  (if (> score (second (get @bests s [nil 0])))
			(swap! bests assoc s [parse score]))))
      (first (get @bests s)))))


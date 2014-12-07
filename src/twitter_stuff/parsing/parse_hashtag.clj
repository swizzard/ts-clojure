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

(defn get-subwords [s] (map #(map (fn [p] (apply str (rest p))) %)
			    (insta/parses subword-parser s :optimize :memory)))

(defn get-score [parse-str]
	(when (< 0 (count parse-str))
	(with-precision 10
	(let [freqs (map #(Math/pow (get word-freqs % oov) 
		             (/ (count %)))
			parse-str)]
		(Math/pow (apply * freqs) (/ (count parse-str)))))))

(defn get-best-parse-map [s]
   (if-let [curr-best (get @bests s)]
	(first curr-best)
        (apply (partial max-key :score) 
		(map (fn [sw] {:parse sw :score (get-score sw)}) 
					(get-subwords s)))))
	
(defn get-best-parse [s]
  (if-let [curr-best (get @bests s)]
    (first curr-best)
    (do
      (doseq [parse (get-subwords s)]
	    (let [score (get-score parse)]
		  (if (> score (second (get @bests s [nil 0])))
			(swap! bests assoc s [parse score]))))
      (first (get @bests s)))))


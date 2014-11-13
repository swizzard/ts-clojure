(ns twitter-stuff.parsing.parse-hashtag
  (:require [instaparse.core :as insta]
            [cheshire.core :refer [parse-stream]]
            [clojure.java.io :as io]))

(def bests (atom {}))

(def word-freqs (parse-stream
                 (io/reader "resources/lowered_freqs.json")))

(def oov (/ (count word-freqs)))

(def subword-parser (insta/parser
                     "<S> = W+
                     W = #'\\w'+"))

(defn get-subwords [s] (pmap
                        #(pmap (fn [p] (apply str (rest p))) %)
                          (insta/parses subword-parser s)))

(defn rank-freqs [ss]
  (let [freqs (pmap #(* (get word-freqs % oov) (count %)) ss)]
  [(Math/pow (apply * freqs) (/ (count ss)))
   ss]))

(defn get-best-parse [s]
  (if-let [curr-best (get bests s)]
    curr-best
    ([let best (last (apply sorted-map
                   (reduce concat
                       (pmap rank-freqs
                             (get-subwords s)))))]
        (swap! bests assoc s best))))

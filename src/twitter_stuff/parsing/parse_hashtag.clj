(ns twitter-stuff.parsing.parse-hashtag
  (:require [instaparse.core :as insta]
            [cheshire.core :refer [parse-stream]]
            [clojure.java.io :as io]))

(def word-freqs (parse-stream
                 (io/reader "resources/lowered_freqs.json")))

(def oov (/ (count word-freqs)))

(def subword-parser (insta/parser
                     "<S> = W+
                     W = #'\\w'+"))

(defn get-subwords [s] (doseq [parses (insta/parses subword-parser s)]
                        (map (fn [p] (apply str (rest p))) parses)))


(defn rank-freqs [ss]
  (let [freqs (map #(* (get word-freqs % oov) (count %)) ss)]
  [(Math/pow (apply * freqs) (/ (count ss)))
   ss]))

(defn get-best-parse [s]
  (last (apply sorted-map
               (reduce concat
                       (map rank-freqs
                             (get-subwords s))))))


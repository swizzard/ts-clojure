(ns twitter-stuff.post-post-processing.hashtag
  (:require (twitter-stuff.utils [analysis :as analysis]
                                 [helpers :as helpers])))

(defn get-ref-vec [ms k] (vec (analysis/reduce-key ms k into #{})))

(defn pos-rep [parsed-text] (let [cnt (count parsed-text)
                                  freqs (frequencies (map :tag parsed-text))]
                                (transform-map freqs #(/ % cnt))))

(defn avg-pos-rep [tweets] (let [poss (reduce-key tweets :parsed-text
                                        (fn [p c] (concat p (map :tag c)))
                                        [])
                                 freqs (frequencies poss)
                                 cnt (count poss)]
                            (transform-map freqs #(double (/ % cnt)))))

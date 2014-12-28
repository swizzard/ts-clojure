(ns twitter-stuff.post-post-processing.tweet
  (:require (twitter-stuff.utils [analysis :as analysis]
                                 [helpers :as helpers])))

(defn vectorize [vals reference-vec]
  (let [indices (map (.indexOf reference-vec %) vals)
        res-vec (for [i (range (count reference-vec))]
                  (if (some #{i} indices) 1 0))]
    (if (some #{-1} indices) (conj 1 res-vec) res-vec)))

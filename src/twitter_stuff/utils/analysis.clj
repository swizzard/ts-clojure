(ns twitter-stuff.utils.analysis
	(:require (monger [collection :as mc] 
			  [operators :as mo])
		  [twitter-stuff.utils.mongo :refer [mongos-conn coll]]))

(defn arith-mean [vals] (double (/ (apply + vals) (count vals))))

(defn geom-mean [vals] (Math/pow (apply * vals) (/ (count vals))))

(defn count-all-tweets [] (mc/count mongos-conn coll))

(defn get-coocs [tag & r] (let [tags (reduce concat (map :hashtags
				   		                                (mc/find-maps mongos-conn coll
						                                   {:hashtags tag})))]
                            (if (some #{:remove-tag} r) 
                                (remove #(= tag %) tags)
                                tags)))

(defn coocs-hops [tag hops]
	(loop [i 0 tags [(get-coocs tag)]]
        (if (<= i hops)
                (recur
                    (inc i)
                    (conj tags (reduce into 
                                    (map get-coocs
                                         (distinct (last tags))))))
                (reduce concat tags))))

(defn cond-prob [tag1 tag2]
    (let [divisor (count-all-tweets)]
        (double
            (/ (/ (mc/count mongos-conn coll {:hashtags {mo/$all [tag1 tag2]}})
               divisor)
            (/ (mc/count mongos-conn coll {:hashtags tag2})
               divisor)))))


(defn get-all-tags [] (reduce into #{} (map :hashtags (mc/find-maps mongos-conn coll))))

(defn get-tweets-with-tag [tag] (mc/find-maps mongos-conn coll {:hashtags tag}))

(defn reduce-key
    ([ms k f] (reduce f (map k ms)))
    ([ms k f c] (reduce f c (map k ms))))

(defn comp-key
    ([ms k f] (f (map k ms)))
    ([ms k f hof] (hof f (map k ms)))) 

(defn transform-map [m f] (reduce (fn [m e] (assoc m (key e) (f (val e)))) {} m))

(defn keys-ratio [m k1 k2] (/ (get m k1) (get m k2)))

(defn pos-rep [parsed-text] (let [cnt (count parsed-text)
                                  freqs (frequencies (map :tag parsed-text))]
                                (transform-map freqs #(/ % cnt))))

(defn avg-pos-rep [tweets] (let [poss (reduce-key tweets :parsed-text
                                        (fn [p c] (concat p (map :tag c)))
                                        [])
                                 freqs (frequencies poss)
                                 cnt (count poss)]
                            (transform-map freqs #(double (/ % cnt)))))

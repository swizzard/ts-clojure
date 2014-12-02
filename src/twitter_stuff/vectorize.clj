(ns twitter-stuff.vectorization.vectorize)

(def words (atom (set nil)))

(def tag-words (atom (set nil)))

(def mentions (atom (set nil)))

(def authors (atom (set nil)))

(def urls (atom (set nil)))

(def domains (atom (set nil)))

(defn add-to-set [a v] (swap! a conj v))

(defn set-to-map [a] (let [s @a] (zipmap s (range (count s)))))

(defn aggregate-vals [ms k] (frequencies (map #(get % k)  ms)))

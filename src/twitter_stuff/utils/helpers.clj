(ns twitter-stuff.utils.helpers
  (:require [clojure.core.match :refer [match]]))

(defn screen-map
  ^{:doc "'screen' a map based on whether a specified key's value
    matches the value provided. Passing a vector of keys as the
    second argument mimics get-in. Omitting the value screens
    falsy values
    ex:
      (def m {:a 1 :b 2 :c 3})
      (screen-map m :a 1) --> {:a 1 :b 2 :c 3}
      (screen-map m :a :foo) --> nil

      (def m2 {:a {:b 1} :c 2})
      (screen-map m2 [:a :b] 1) --> {:a {:b 1} :c 2}
      (screen-map m2 [:a :b] 2) --> nil

      (def m3 {:a 1 :b 2})
      (screen-map m :a) --> {:a 1 :b 2}
      (screen-map :c) --> nil"}
  ([m k-or-ks v]
   (match [(nil? m) (sequential? k-or-ks)]
     [false true]
       (if (= (get-in m k-or-ks) v)
         m)
     [false false]
       (if (= (get m k-or-ks) v)
         m)
     :else nil))
  ([m k-or-ks]
   (match [(nil? m) (sequential? k-or-ks)]
     [false true]
       (if (seq (get-in m k-or-ks)) m)
     [false false]
       (if (seq (get m k-or-ks)) m)
     :else
       nil)))

(defn eng-only [m] (screen-map m :lang "en"))

(defn has-text [m] (screen-map m :text))

(defn has-tags [m] (screen-map m [:entities :hashtags]))

(defn screen-tweet [t] (-> t eng-only has-text has-tags))

(defn from-q [process-fn q]
    (loop [res (.take q)]
      (process-fn res)
      (recur (.take q))))

(defn to-q [process-fn q]
    (fn [input]
      (if-let [res (process-fn input)]
        (.offer q res))))

(defn q-to-q [process-fn in-q out-q]
    (from-q (to-q process-fn out-q) in-q))

(defn map-by [f coll] (reduce (partial apply assoc) {} (map f coll)))

(defn max-by 
    ^{:doc "Get the 'max' entry of a dictionary, as defined by 
      a comparator function that operates on either keys or values"}
    [c k-or-v m]
    (let [cmp-target (match [k-or-v]
                            [:k] 0
                            [:key] 0
                            [:v] 1
                            [:val] 1
                            :else k-or-v)]
        (loop [h (first m) t (next m) mx (first m)]
            (if t (recur (first t) 
                         (next t) 
                         (if (c (get h cmp-target) (get mx cmp-target)) h mx))
                mx))))


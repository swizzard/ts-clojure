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
      (screen-map m2 [:a :b] 2) --> nil"}
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
    (comp #(doseq [res %] (.offer q res)) process-fn))

(defn q-to-q [process-fn in-q out-q]
    (from-q (to-q process-fn out-q) in-q))

(defn map-by [f coll] (reduce (partial apply assoc) {} (map f coll)))

(defn sort-docs-by-tag [docs] (map-by (fn [ht] [(:key ht) (get-in ht [:doc :tweets])]) docs))

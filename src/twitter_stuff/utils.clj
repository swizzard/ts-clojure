(ns twitter-stuff.utils
  (:require [com.ashafa.clutch :as clutch]
            [environ.core :refer [env]]
            [cheshire.core :refer [parse-stream]]
            [clojure.java.io :as io]
            [instaparse.core :as insta]
            (clj-time format predicates)
            [clj-http.client :as client])
  (:import (com.twitter.hbc httpclient.auth.OAuth1)))

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
   (if (sequential? k-or-ks)
     (if (= (get-in m k-or-ks) v)
       m)
     (if (= (get m k-or-ks) v)
       m)))
  ([m k-or-ks]
   (if (sequential? k-or-ks)
     (if (some? (get-in m k-or-ks)) m)
     (if (some? (get m k-or-ks)) m))))

(defn conj-doc [db id k addition]
  (if-let [doc (clutch/get-document db id)]
    (let [v (get doc k)
          f (cond (sequential? v) (fn [v a] (conj v a))
                  (nil? v) (fn [_ a] a)
                  :else (fn [v a] (conj (vector v) a)))
          updated (assoc doc k (f v addition))
          rev (:_rev doc)]
      (clutch/put-document db (assoc updated :_id id :_rev rev))))
    (clutch/put-document db (assoc {:_id id} k addition)))

(defn assoc-doc [db id m]
    (couch/put-doc db
     (merge (couch/get-doc db id) m)))


(def word-freqs (parse-stream (io/reader "resources/lowered_freqs.json")))

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
  (last (apply sorted-map (reduce concat (pmap rank-freqs (get-subwords s))))))

(def caf (clj-time.format/formatter "E MMMM dd HH:mm:ss Z yyyy"))

(defn get-date-info [date-str]
  (let [dobj (clj-time.format/parse caf date-str)]
    {:is-weekday (clj-time.predicates/weekday? dobj)
     :day-of-week (.get (.dayOfWeek dobj))
     :day-of-month (.get (.dayOfMonth dobj))
     :year (.get (.year dobj))}))


(defn expand-url [url-entity]
  (last (:trace-redirects (client/get (:expanded-url url-entity)))))

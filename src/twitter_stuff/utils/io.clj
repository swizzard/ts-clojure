(ns twitter-stuff.utils.io
  (:require [com.ashafa.clutch :as clutch]
            [environ.core :refer [env]]
            [cheshire.core :refer [generate-string
                                   parse-string]]
            [clojure.java.io :as io])
  (:import (com.twitter.hbc httpclient.auth.OAuth1)))


(defn load-tweets [f]
  (with-open [r (clojure.java.io/reader f)]
    (map #(parse-string % true) (doall (line-seq r)))))


(defn write-tweets-lbq
  [lbq out-file & [max-written]]
    (let [mx (or max-written 10)]
      (with-open [w (clojure.java.io/writer out-file :append true)]
         (dorun
          (repeatedly mx
                      #(.write w
                               (str
                                (generate-string (.take lbq)) "\n")))))))

(defn write-tweets-agent
  [a out-file]
  (with-open [w (clojure.java.io/writer out-file :append true)]
      (dorun (map #(.write w (str (generate-string %) "\n")) @a))))

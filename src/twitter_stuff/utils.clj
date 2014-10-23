(ns twitter-stuff.utils
  (:require [com.ashafa.clutch :as clutch]
           [environ.core :refer [env]])
  (:import (com.twitter.hbc httpclient.auth.OAuth1)))

(defn screen-map
  ^{:doc "'screen' a map based on whether a specified key's value
    matches the value provided. ex:
      (def m {:a 1 :b 2 :c 3})
      (screen-map m :a 1) --> {:a 1 :b 2 :c 3}
      (screen-map m :a :foo) --> nil"}
  [map key val]
  (if (= (key map) val)
    map))

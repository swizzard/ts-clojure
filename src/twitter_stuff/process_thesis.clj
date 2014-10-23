(ns twitter-stuff.process-thesis
  (:require (twitter-stuff
             [utils :refer [screen-map]]
             ))
  )

(defn eng-only [m] (screen-map m :lang "en"))

(ns twitter-stuff.tagger
  (:import [cmu.arktweetnlp Tagger]))

(def tagger (doto (new Tagger) (.loadModel "resources/model.20120919")))

(defn tag [tagger text] (.tokenizeAndTag tagger text))

(defn tag-map [tags] (map (fn [t] {:token (.token t) :tag (.tag t)}) tags))

(defn text-to-tag-map [text] (tag-map (tag tagger text)))

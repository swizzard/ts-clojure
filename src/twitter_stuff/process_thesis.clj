(ns twitter-stuff.process-thesis
  (:require (twitter-stuff [utils :as utils]
                           [couch :refer [db]]
                           [tagger :refer [text-to-tag-map]]
                           [parse-user :refer [process-user]]
                           )
            [com.ashafa.clutch :refer [with-db]]
  ))

(defn eng-only [m] (utils/screen-map m :lang "en"))

(defn tag-text [text]
  (tagger/text-to-tag-map text))

(defn process-tweet [t]
  (-> t
      (update-in [:entities :urls]
                 #(mapv expand-url %))
      (update-in [:text]
                 (tagger/text-to-tag-map text))
      (merge (utils/get-date-info (:created-at t)))
      (update-in [:user] process-user)
      ))

(defn tweet-to-hashtags [t]
  (if-let [tweet (screen-map t [:entities :hashtags])]
    (map #({:hashtag %
            :tweet (process-tweet tweet)}) (get-in
                                       tweet
                                       [:entities :hashtags]))))

(defn update-hashtags [tweet]
  (if-let [hts (tweet-to-hashtags tweet)]
    (map
     (do
       #(utils/conj-doc db
                          (:hashtag %)
                          :tweets
                          (:tweet %))
       #(utils/assoc-doc db (:hashtag %)
                         {:split-hashtag
                          (utils/get-best-parse %)}))
         hts)))



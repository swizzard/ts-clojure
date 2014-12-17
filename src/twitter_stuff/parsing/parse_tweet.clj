(ns twitter-stuff.parsing.parse-tweet
  (:require (twitter-stuff.utils
                           [tagger :refer [parse-tweet-text]]
			   [mongo :refer [hashtags-to-mongo]])
                           [helpers :as helpers]
            (twitter-stuff.parsing [parse-hashtag :refer
                                    [get-best-parse]]
                                   [parse-etc :refer
                                    [get-expanded-urls
                                     get-date-info]]
                                   [parse-user :refer
                                    [process-user]])
        [cheshire.core :refer [parse-string]]
	    [clojure.string :as string]))

(defn process-tweet [tw]
  (if-let [t (-> (parse-string tw true) helpers/screen-tweet)]
  (-> t
      (assoc :expanded_urls
	     (get-expanded-urls (get-in t [:entities :urls])))
      (merge (parse-tweet-text (:text t)))
      (merge (get-date-info (:created_at t)))
      (update-in [:user] process-user)
      )))

(defn tweet-to-hashtags [t]
  (if-let [tweet (-> (if (string? t) (parse-string t :true) t)
                       helpers/has-text
                       helpers/eng-only
                       helpers/has-tags)]
    (map (fn [ht] {:hashtag (string/replace (:text ht) #"\A_|_\z" "\\\\_")
		    :tweet (process-tweet tweet)})
          (get-in tweet [:entities :hashtags]))))

(defn tweet-to-db [db t]
  (hashtags-to-db db (tweet-to-hashtags t)))

(defn tweet-to-mongo [t]
	(hashtags-to-mongo (tweet-to-hashtags t)))

(ns twitter-stuff.parsing.parse-tweet
  (:require (twitter-stuff.utils
                           [tagger :refer [parse-tweet-text]]
                           [helpers :refer [screen-map
                                            eng-only
                                            has-text
                                            has-tags]]
                           [couch :refer [hashtags-to-db]])
            (twitter-stuff.parsing [parse-hashtag :refer
                                    [get-best-parse]]
                                   [parse-etc :refer
                                    [get-expanded-urls
                                     get-date-info]]
                                   [parse-user :refer
                                    [process-user]])))

(defn process-tweet [t]
  (-> t
      (assoc :expanded_urls
	     (get-expanded-urls (get-in t [:entities :urls])))
      (merge (parse-tweet-text (:text t)))
      (merge (get-date-info (:created_at t)))
      (update-in [:user] process-user)
      ))

(defn tweet-to-hashtags [t]
  (if-let [tweet (-> t has-text
                       eng-only
                       has-tags)]
    (map (fn [ht] {:hashtag (:text ht) 
		    :tweet (process-tweet tweet)})
          (get-in tweet [:entities :hashtags]))))

(defn update-hashtags [tweet]
  (if-let [hts (tweet-to-hashtags tweet)]
    (map (fn [htm] (let [hashtag (:hashtag htm)]
                     {:hashtag hashtag
                     :split-hashtag (get-best-parse hashtag)
		     :tweet (:tweet htm)}))
     hts)))

(defn tweet-to-db [db t]
  (hashtags-to-db db (update-hashtags t)))


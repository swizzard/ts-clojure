(ns twitter-stuff.parsing.parse-tweet
  (:require (twitter-stuff.utils
                           [tagger :refer [parse-tweet-text]]
                           [helpers :as helpers])
            (twitter-stuff.parsing [parse-hashtag :refer
                                    [get-best-parse]]
                                   [parse-etc :refer
                                    [get-expanded-urls
                                     get-date-info]]
                                   [parse-user :refer
                                    [process-user]])
        [cheshire.core :refer [parse-string]]))

(defn process-tweet [tw]
  (if-let [t (-> (parse-string tw true) helpers/screen-tweet)]
  (-> t
      (assoc :expanded_urls
	     (get-expanded-urls (get-in t [:entities :urls])))
      (merge (parse-tweet-text (:text t)))
      (merge (get-date-info (:created_at t)))
      (update-in [:user] process-user)
      )))


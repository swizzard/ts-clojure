(ns twitter-stuff.parse-user
  (:require [twitter-stuff.utils :as utils]))

(defn process-user [user]
  (-> user
      (update-in [:entities :url :urls]
                 #(map utils/expand-url %))
      (update-in [:entities :descrption :urls]
                 #(map utils/expand-url %))
      (assoc :follow-ratio (double
                            (/
                             (:followers-count user)
                             (:friends-count user))))
      ))

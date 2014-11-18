(ns twitter-stuff.main
  (:require [twitter-stuff.twitter.twitter :as twitter]
            [twitter-stuff.parsing.parse-tweet :as pt]
            [twitter-stuff.utils.couch :refer [db]])
  (:import [java.util.concurrent.LinkedBlockingQueue]))

(def mq (java.util.concurrent.LinkedBlockingQueue.))
(def rq (java.util.concurrent.LinkedBlockingQueue.))
(def rqa (agent nil))

(def client (twitter/create-client twitter/auth (twitter/get-endpoint) mq))

(defn process [& {:keys [use-agent nores] :or {use-agent false
					       nores false}}]
  (if use-agent
   (twitter/process-stream mq rqa (partial pt/tweet-to-db db))
   (if nores
	(twitter/process-stream-nores mq (partial pt/tweet-to-db db))
   (twitter/process-stream-lbq mq rq (partial pt/tweet-to-db db)))))

(def t (Thread. #(process :use-agent true)))

(defn run [t] (do
              (.connect client)
              (.start t)
                t))

(defn stop [t] (twitter/stop-client client t))

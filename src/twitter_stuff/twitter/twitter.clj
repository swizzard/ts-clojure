(ns twitter-stuff.twitter.twitter
  (:import (com.twitter.hbc
                             ClientBuilder
                             core.Client
                             core.Constants
                             core.endpoint.StatusesFilterEndpoint
                             core.endpoint.StatusesSampleEndpoint
                             core.processor.StringDelimitedProcessor)
           (java.util.concurrent LinkedBlockingQueue
                                 TimeUnit))
  (:require [clojure.core.match :refer [match]]
            [cheshire.core :refer [parse-string]]
            [twitter-stuff.twitter.auth :refer [env-auth]]
            [twitter-stuff.utils.mq :refer [get-msg-queue
                                            event-queue]]))




(def auth (env-auth))

(defn get-endpoint [& {:keys [followings terms] :or {followings nil terms nil}}]
  (if (every? nil? [followings terms])
    (StatusesSampleEndpoint.)
    (-> (StatusesFilterEndpoint.)
        ((if (seq followings) (fn [ep] (.followings ep followings))
           (fn [ep] ep)))
        ((if (seq terms) (fn [ep] (.trackTerms ep terms))
           (fn [ep] ep))))))


(defn create-client [auth ep mq]
    (-> (ClientBuilder.)
      (.hosts Constants/STREAM_HOST)
      (.endpoint ep)
      (.authentication auth)
      (.processor (StringDelimitedProcessor. mq))
      (.eventMessageQueue event-queue)
      (.build)))

(defn get-client [mq] (create-client auth (get-endpoint) mq))

(defn connect-client [client] (.connect client))

(defn stop-client
  ([client] (.stop client))
  ([client processor-thread]
   (do
     (.stop client)
     (.stop processor-thread))))

(defn process-tweets [mq rq] (process-stream mq rq))

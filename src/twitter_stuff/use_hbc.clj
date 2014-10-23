(ns twitter-stuff.use-hbc
  (:import (com.twitter.hbc
                             ClientBuilder
                             core.Client
                             core.Constants
                             core.endpoint.StatusesFilterEndpoint
                             core.endpoint.StatusesSampleEndpoint
                             core.processor.StringDelimitedProcessor)
           (java.util.concurrent.LinkedBlockingQueue))
  (:require [clojure.core.match :refer [match]]
            [cheshire.core :refer [parse-string]]
            [twitter-stuff.auth :refer env-auth]))

(def msg-queue (let [mqa (agent nil)
                     p (proxy [java.util.concurrent.LinkedBlockingQueue] []
                         (offer [^String string ^long timeout unit]
                                (do
                                  (send-off mqa conj string)
                                  true))
                         (take []
                               (do
                                 (send-off mqa rest)
                                 (first @mqa))))]
    p))

(def event-queue (java.util.concurrent.LinkedBlockingQueue. 1000))

(def auth (get-auth consumer-key consumer-secret access-token access-secret))
(defn get-endpoint [& {:keys [followings terms] :or {followings nil terms nil}}]
  (do (println followings terms)
  (if (every? nil? [followings terms])
    (StatusesSampleEndpoint.)
    (-> (StatusesFilterEndpoint.)
        ((if (seq followings) (fn [ep] (.followings ep followings))
           (fn [ep] ep)))
        ((if (seq terms) (fn [ep] (.trackTerms ep terms))
           (fn [ep] ep)))))))


(defn create-client [auth ep]
    (-> (ClientBuilder.)
      (.hosts Constants/STREAM_HOST)
      (.endpoint ep)
      (.authentication auth)
      (.processor (StringDelimitedProcessor. msg-queue))
      (.eventMessageQueue event-queue)
      (.build)))


(defn get-client [] (create-client auth (get-endpoint)))

(defn connect-client [client] (.connect client))
(defn stop-client [client] (do
                             (.stop client)
                             (shutdown-agents)))

(defn process-stream [queue results-queue & [process-fn]]
  (let [to-map (fn [q] (parse-string (.take q) true))
        process (if process-fn
                  (comp process-fn to-map)
                  to-map)]
    (while true
      (if-let [result (process queue)]
          (send-off
           results-queue
           conj
           result)))))

(def results-queue (agent nil))

(defn process-tweets [] (process-stream msg-queue results-queue))

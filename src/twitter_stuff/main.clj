(ns twitter-stuff.main
  (:require [twitter-stuff.twitter.twitter :as twitter]
            [twitter-stuff.parsing.parse-tweet :as pt]
            [twitter-stuff.utils.couch :refer [get-db]])
  (:import [java.util.concurrent.LinkedBlockingQueue]))

(def mq (java.util.concurrent.LinkedBlockingQueue.))
(def rq (java.util.concurrent.LinkedBlockingQueue.))
(def rqa (agent nil))

(def client (twitter/create-client twitter/auth (twitter/get-endpoint) mq))

(defn process [] (let [db (get-db)]
                   (twitter/process-stream mq (partial pt/tweet-to-db db))))

(defn process-mongo [] (twitter/process-stream mq (partial pt/tweet-to-mongo)))

(def t (Thread. #(process)))

(def m-t (Thread. #(process-mongo)))

(defn run [t] (do
              (.connect client)
              (.start t)
                t))

(defn stop [t] (twitter/stop-client client t))

(defn -main [] (run t))

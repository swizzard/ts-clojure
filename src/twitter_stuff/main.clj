(ns twitter-stuff.main
  (:require [twitter-stuff.twitter.twitter :refer [get-client]]
            [twitter-stuff.parsing.parse-tweet :refer [process-tweet]]
	    [twitter-stuff.utils.mongo :refer [tweet-to-mongo mongos-conn]]
            [twitter-stuff.utils.helpers :refer [from-q q-to-q]]
            [environ.core :refer [env]]
	    [monger.core :as mg]))

(def mq (java.util.concurrent.LinkedBlockingQueue.))
(def rq (java.util.concurrent.LinkedBlockingQueue.))
(def client (get-client mq))

(defn process [] (q-to-q process-tweet mq rq))
(defn upload [db] (from-q #(tweet-to-mongo db %) rq))

(defn get-threads [num-proc num-up] 
	{:process (repeat num-proc (Thread. process))
         :upload (repeat 3 
		   (Thread. 
			#(upload mongos-conn)))}) 

(defn start-all [threads]
	(doseq [t threads]
		(try
			(.start t)
		(catch Exception e))))

(defn run [num-proc num-up]
           (let [threads (get-threads num-proc num-up)
                 client (get-client mq)]
             (do
                (.connect client)
		        (start-all (:process threads))
                        (Thread/sleep 1000)
		        (start-all (:upload threads))
                {:client client :threads threads})))

(defn stop [m] (let [threads (:threads m)] 
                (do
                  (.stop (:client m))
                  (map #(.stop %) (:process threads))
                  (map #(.stop %) (:upload threads)))))

(defn cycle-threads [threads interval]
    (do
        (println "Cycling threads in " interval)
        (Thread/sleep interval)
        (println "Stopping threads")
        (stop threads)
        (println "Regenerating threads")
        (run 1 1)))

(defn -main [] (loop [ts (run 1 3)] (recur (cycle-threads ts 300000))))

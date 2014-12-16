(ns twitter-stuff.main
  (:require [twitter-stuff.twitter.twitter :refer [get-client]]
            [twitter-stuff.parsing.parse-tweet :as pt]
            [twitter-stuff.utils.couch :refer [get-db]] 
            [twitter-stuff.utils.helpers :refer [from-q q-to-q]]
            [com.ashafa.clutch :as clutch]
            [environ.core :refer [env]]))

(def db (get-db))
(def mq (java.util.concurrent.LinkedBlockingQueue.))
(def rq (java.util.concurrent.LinkedBlockingQueue.))


(defn process [] (q-to-q pt/process-tweet mq rq))
(defn upload [] (from-q #(clutch/put-document db %) rq))

(defn get-threads [num-proc num-up] {:process (repeat num-proc (Thread. #(process)))
                                     :upload (repeat num-up (Thread. #(upload)))})
(def processor (Thread. #(process)))
(def uploader (Thread. #(upload)))

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

(defn -main [] (run 6 6))

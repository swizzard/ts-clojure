(ns twitter-stuff.utils.mq
  (:import (java.util.concurrent LinkedBlockingQueue
                                 TimeUnit)))

(defn get-msg-queue [] (let [mqa (agent nil)
                         p (proxy [java.util.concurrent.LinkedBlockingQueue
                                   clojure.lang.ISeq] []
                             (offer [^String string ^long timeout
                                     ^java.util.concurrent.TimeUnit unit]
                                    (do
                                      (send-off mqa conj string)
                                      true))
                             (take []
                                   (do
                                     (first @mqa)
                                     (send-off mqa rest)
                                     ))
                             (seq [] @mqa)
                             (first [] (first @mqa))
                             (rest [] (rest @mqa)))]
                         p))

(def event-queue (java.util.concurrent.LinkedBlockingQueue. 1000))

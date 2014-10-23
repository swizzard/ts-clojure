(defproject twitter-stuff "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.twitter/hbc-core "2.2.0"]
                 [com.google.guava/guava "18.0"]
                 [org.clojure/core.match "0.2.1"]
                 [edu.cmu.cs/ark-tweet-nlp "0.3.2"]
                 [com.ashafa/clutch "0.4.0"]
                 [com.cemerick/url "0.0.6"]
                 [clj-http "0.5.5"]
                 [environ "1.0.0"]]
  :plugins [[lein-gorilla "0.3.3"]
            [lein-environ "1.0.0"]
            [lein-pprint "1.1.1"]])

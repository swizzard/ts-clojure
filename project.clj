(defproject twitter-stuff "0.1.1-SNAPSHOT"
  :description "Twitter processing stuff"
  :url "http://samraker.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.twitter/hbc-core "2.2.0"]
                 [com.google.guava/guava "18.0"]
                 [org.clojure/core.match "0.2.1"]
                 [edu.cmu.cs/ark-tweet-nlp "0.3.2"]
                 [com.ashafa/clutch "0.4.0"]
                 [com.cemerick/url "0.0.6"]
                 [clj-http "1.0.0"]
                 [environ "1.0.0"]
                 [org.slf4j/slf4j-api "1.7.7"]
                 [org.slf4j/slf4j-simple "1.6.1"]
                 [org.apache.httpcomponents/httpclient "4.3.5"]
                 [instaparse "1.3.4"]
                 [clj-time "0.8.0"]]
  :plugins [[lein-gorilla "0.3.3"]
            [lein-environ "1.0.0"]
            [lein-pprint "1.1.1"]]
  :jvm-opts ["-Xmx16g" "-Xms2g" "-server"])

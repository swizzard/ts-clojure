(ns twitter-stuff.parsing.parse-etc
  (:require (clj-time format predicates)
            [clj-http.client :as client]
            [cemerick.url :refer [url]]))

(def caf (clj-time.format/formatter "E MMMM dd HH:mm:ss Z yyyy"))

(defn get-date-info [date-str]
  (let [caf (clj-time.format/formatter "E MMMM dd HH:mm:ss Z yyyy")
        dobj (clj-time.format/parse caf date-str)]
    {:is-weekday (clj-time.predicates/weekday? dobj)
     :day-of-week (.get (.dayOfWeek dobj))
     :day-of-month (.get (.dayOfMonth dobj))
     :year (.get (.year dobj))}))

(defn expand-url [u]
  (if u
  (let [resolved (last (:trace-redirects (client/get u
                               {:throw-exceptions false})))]
  (try (url resolved)
       (catch Exception e resolved)))))

(defn get-expanded-urls [urls]
  {:parsed-urls
   (if urls
     (mapv (fn [url-entity] (if-let [expanded (expand-url
                                            (:expanded_url url-entity))]
            {:url (:display_url url-entity)
             :expanded-url (str expanded)
             :domain (or (:host expanded)
                          (last
                           (re-find #"https?://(?:www.)?([\w\.]+)"
                                    expanded)))}))
          urls)
     nil)})

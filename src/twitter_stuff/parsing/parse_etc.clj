(ns twitter-stuff.parsing.parse-etc
  (:require (clj-time format predicates)
            [clj-http.client :as client]
            [cemerick.url :refer [url]]))

(def caf (clj-time.format/formatter "E MMMM dd HH:mm:ss Z yyyy"))

(defn get-date-info [date-str]
  (let [dobj (clj-time.format/parse caf date-str)]
    {:is-weekday (clj-time.predicates/weekday? dobj)
     :day-of-week (.get (.dayOfWeek dobj))
     :day-of-month (.get (.dayOfMonth dobj))
     :year (.get (.year dobj))}))

(defn expand-url [u]
    (if u
      (let [resolved (last (:trace-redirects (try
     					       (client/get u
                                                 {:throw-exceptions false})
				               (catch Exception e
					     nil))))]
  (try (url resolved)
       (catch Exception e resolved)))))

(def host-pat #"https://(?:www.)?([\w\.]+)")

(defn get-expanded-url [entity-or-url]
	(if (map? entity-or-url)
		(let [expanded (expand-url (:expanded_url entity-or-url))]
			{:url (:display_url entity-or-url)
			 :expanded_url (str expanded)
			 :host (or (:host expanded)
				   (last (re-find #"https?://(?:www.)?([\w\.]+)" (str entity-or-url))))})
		(let [expanded (expand-url entity-or-url)]
			{:url entity-or-url
			 :expanded_url (str expanded)
			 :host (or (:host expanded)
				   (last (re-find #"https?://(?:www.)?([\w\.]+)" (str entity-or-url))))})))

(defn get-expanded-urls [url-or-urls]
	(if ((every-pred coll? (complement map?)) url-or-urls) 
				(map get-expanded-url url-or-urls)
			 	(get-expanded-url url-or-urls)))

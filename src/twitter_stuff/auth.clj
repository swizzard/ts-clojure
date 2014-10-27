(ns twitter-stuff.auth
  (:require [environ.core :refer [env]])
  (:import (com.twitter.hbc httpclient.auth.OAuth1)))

(defn ^{:doc "create OAuth authorization object from
        strings"}
  get-auth [consumer-key consumer-secret
                access-token access-secret]
  (OAuth1. consumer-key consumer-key access-token access-secret))

(defn ^{:doc "create OAuth authorization object from environment
       variables. pass it the environment variable names of
       your OAuth credentials as keywords, e.g.
       (env-auth :consumer-key-varname :cons-key
                 :consumer-secret-varname :consumer-sec
                 :access-token-varname :acc-token
                 :access-secret-varname :acc-secret)"}
  env-auth [& {:keys [consumer-key-varname consumer-secret-varname
                          access-token-varname access-secret-varname]
                   :or {consumer-key-varname :consumer-key
                        consumer-secret-varname :consumer-secret
                        access-token-varname :access-token
                        access-secret-varname :access-secret}}]
            (com.twitter.hbc.httpclient.auth.OAuth1.
              (env consumer-key-varname)
              (env consumer-secret-varname)
              (env access-token-varname)
              (env access-secret-varname)))

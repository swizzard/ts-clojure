(ns twitter-stuff.client
  (:require [twitter.oauth :as oauth]))

(def ^:private consumer-key "BaAzdzhmeOyNoDdc71gVmazSV")
(def ^:private consumer-secret "GsmVan290xRf5mY6Yo0h6nGuHpbv1CTlwTMeyThhULXarsYBqm")
(def ^:private access-token "2784894884-8UVZJidK1UgyfwnrlHTNnRumJzQJKZzxzFe9jec")
(def ^:private access-secret "sD2cVfCgQWy1fTWad8pVtmrUFPuCz7ZNkOMcTAVmKM11B")

(def creds (oauth/make-oauth-creds consumer-key
                                   consumer-secret
                                   access-token
                                   access-secret))

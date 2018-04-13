(ns browse-scaler.util
  (:require
    [cljs.core.async :as async]
    [cljs.nodejs :as nodejs]
    [cljs.pprint :refer [pprint]]
    [cljs.reader :refer [read-string]]
    [clojure.string :as string])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def fs (nodejs/require "fs"))
(def stream (js/require "stream"))
(def js-url (js/require "url"))
(def ^:const eol (.-EOL (js/require "os")))

(defn node-slurp
  [path]
  (.readFileSync fs path "utf8"))

(def config (node-slurp "static/config.edn"))
(def cmr-search-root (:cmr-search-root config))
(def browse-rel "http://esipfed.org/ns/fedsearch/1.1/browse#")
(def tmp-image-dir "/tmp")

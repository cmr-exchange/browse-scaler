(ns browse-scaler.api.caching.impl.s3
  (:require
    [cljs.core.async :as async :refer [<!]]
    [cljs.pprint :refer [pprint]]
    [clojure.string :as string]
    [cljs.pprint :as pprint]
    [promesa.core :as p])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce AWS (js/require "aws-sdk"))
(def s3-bucket-name (:s3-bucket-name util/config))
(def s3-base-url (str "https://s3.amazonaws.com/" s3-bucket-name))
(defn s3-client []
  (new AWS.S3))

(defn pretty-print-result-handler [error result]
  (if error
    (println "ERROR !!!" error)
    (println (pprint/pprint (js->clj result)))))

(defn s3-url
  "Return the S3 URL for a file basename"
  [basename]
  (str s3-base-url "/" basename))

(defn cache-in-s3
  "Save a file into S3"
  [path]
  (let [key (last (string/split path #"/"))]
    (println "s3/put-object  " s3-bucket-name " " key)
    (s3-client/waitFor "putObject" (clj->js {:Bucket s3-bucket-name :Key key}))
    (s3-url key)))

(defn get-from-s3
  [path-or-key]
  (let [key (last (string/split path-or-key #"/"))]
    (s3-client/waitFor "getObject" (clj->js {:Bucket s3-bucket-name :Key key}))))

(defn exists-in-s3?
  [path-or-key]
  (let [params (clj->js {:Bucket s3-bucket-name :Key key})
        key (last (string/split path-or-key #"/"))]
    (s3-client/waitFor "objectExists" params true?)))

(defrecord S3Client [])

(def s3-operations
  {:cache-item cache-in-s3
   :get-item get-from-s3
   :exists? exists-in-s3?})

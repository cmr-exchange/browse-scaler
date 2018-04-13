(ns browse-scaler.api.caching.core
  (:require
   [browse-scaler.api.caching.impl.s3 :as s3]
   [browse-scaler.api.caching.impl.mem :as mem])
  (:import
   (browse-scaler.api.caching.impl.s3 S3Client)))

(defprotocol ImageCache
  "Functions for interfacing with the image cache service"

  (cache-item
   [this item]
   "Put item into cache for later retrieval")

  (get-item
   [this item]
   "Given a path or key, retrieve corresponding item from cache")

  (exists?
   [this item]
   "Given a path or key, find whether or not an object is cached"))

(extend ImageCache
        S3Client
        s3/s3-operations)

(extend ImageCache
        MemoryCache
        mem/cache-operations)

(ns browse-scaler.api.caching.impl.mem
  (:require
    [browse-scaler.util :as util]))

(defonce mem-cache (atom {}))

(defn- download-image
  "Download image to working dir"
  [path-to-image]
  (util/node-slurp path-to-image))

(defn cache-in-memory
  "Populate cache with given item. If the item exists, refresh existing entry"
  [key item]
  (swap! mem-cache assoc key (download-image item)))

(defn get-from-memory
  "Check cache for given item. If the item exists, retrieve it"
  [key]
  (when (exists-in-memory? key)
    (get mem-cache key)))

(defn exists-in-memory?
  [key]
  (some? (get mem-cache key)))

(defrecord MemoryCache [])

(def cache-operations
  {:cache-item cache-in-memory
   :get-item get-from-memory
   :exists? exists-in-memory?})

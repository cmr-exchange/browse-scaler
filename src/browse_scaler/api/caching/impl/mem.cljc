(ns browse-scaler.api.caching.impl.mem
  (:require
    [browse-scaler.util :as util]))

(defonce mem-cache (atom {}))

(defn- download-image
  "Download image to working dir"
  [path-to-image]
  (util/node-slurp path-to-image))

(defn exists-in-memory?
  "Check cache for item with given key. If the item exists and is not expired, return true"
  [key]
  (let [image (get mem-cache key)
        now (.getTime (js/Date.))]
    (some? image)))
    ; (and (some? image)
    ;      ((:created-at image)))))

(defn cache-in-memory
  "Populate cache with given item. If the item exists, refresh existing entry"
  [key item]
  (swap! mem-cache assoc key {:id key
                              :image (download-image item)
                              :created-at (.getTime (js/Date.))}))

(defn get-from-memory
  "Check cache for given item.
  If the item exists and is not older than 60 minutes, retrieve it"
  [key]
  (when (exists-in-memory? key)
    (get mem-cache key)))

(defrecord MemoryCache [])

(def cache-operations
  {:cache-item cache-in-memory
   :exists? exists-in-memory?
   :get-item get-from-memory})

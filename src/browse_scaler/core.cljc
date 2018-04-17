(ns browse-scaler.core
  (:require
    [browse-scaler.util :as util]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [browse-scaler.api.caching.core :as caching])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(require 'sharp)

(defn- get-concept-metadata
  "Given a concept-id, return json metadata"
  [concept-id]
  (go (let [response (<! (http/get
                           (format "%s/concepts/%s.json"
                                   util/cmr-search-root
                                   concept-id)))]
        (:body response))))

(defn- browse-urls-for-concept
  "Given a concept-id, return any browse-urls belonging to said concept"
  [concept-id]
  (let [all-links (get (get-concept-metadata concept-id) "links")
        browse-links (filter #(= (get % "rel") util/browse-rel) all-links)]
    (map #(get % "href") browse-links)))

(defn- full-res-filename
  ""
  [concept-id index]
  (str concept-id "-" index))

(defn- scaled-filename
  ""
  [concept-id index height width]
  (str concept-id "-" index "-" height "x" width))

(defn- valid?
  "Validate a downloaded browse file."
  [path]
  (let [valid-out 1 ;(exec "identify " path)
        exit-code (:exit valid-out)
        std-err (:err valid-out)]
    (when (> 0 exit-code) (printf "Validation failed with [%s]" std-err))
    (= 0 exit-code)))

(defn- download-file
  "Download a browse image from a URL to the tmp dir."
  [url dest]
  (let [ url (string/replace url #"http:" "https:")
        ;; TODO handle http-> https better than just string replacing
        basename (last (string/split dest #"/"))]
    ;;TODO dont download if it is already there
    ;;avoid collisions of same named files (use a hash of some sort)
    (println "download " url)
    (if (caching/exists? basename)
      ;;Download from S3
      (with-open [in (caching/get-item basename)]
        (println "Downloading from s3 to " dest)
        (.copyFileSync fs in (File. dest)))
      ;;Download from provider
      (with-open [in  (.read stream (.parse js-url url))]
        (println "Downloading " url " to " dest)
        (.copyFileSync fs in dest)))
    (println "Returning " dest)
    (if (valid? dest)
      (do
        (caching/cache-item dest)
        dest)
      ;;TODO do something more useful than return a nil if file invalid
      nil)))

(defn- scaled-url
  ""
  [in-path height width]
  (let [scaled-file-key (scaled-filename)])
  (if (caching/exists? (scaled-filename))
    (s3-url)))

(defn- get-scaled-browse-url
  ""
  [concept-id index height width]
  (println "concept-id = " concept-id " index = " index " h = " height " w = " width)
  (let [full-res-filename (full-res-filename concept-id index)
        scaled-filename (scaled-filename concept-id index height width)]
    (if (caching/exists? scaled-filename)
      ;;If its already in s3, return the URL
      (s3-url scaled-filename)
      ;;Else scale and push to s3
      (let [remote-browse-url (-> (browse-urls-for-concept concept-id)
                                  (nth index))
            local-raw-path (download-file remote-browse-url (str tmp-image-dir "/" full-res-filename))
            local-scaled-path (resize local-raw-path height width)]
        (cache-in-s3 local-scaled-path)))))

(defn- resize-image
  ""
  [image height width]
  (-> image
      sharp
      (.resize width height)
      (.toFile (str image "-" width "x" height))))

(defn- concept->browse-image
  "Given a concept-id, return a url to a browse-image to be resized"
  [concept-id])

(defn resize-image-from-concept
  "Given a concept-id, resize the browse imagery to specified dimensions"
  [concept-id height width]
  (resize-image (concept->browse-image concept_id) height width))


(comment
  (get-scaled-browse-url "G1048350149-LPDAAC_ECS" 2 300 300)
  (core/get-concept-metadata "G1048350149-LPDAAC_ECS-0")
  (exists-in-s3? "G1048350149-LPDAAC_ECS-2")
  (get-from-s3 "G1048350149-LPDAAC_ECS-2"))

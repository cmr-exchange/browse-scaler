(ns browse-scaler.handler
  (:require
    [browse-scaler.core :as core]))

(defn ^:export handle-event [event]
  (println "Got event:")
  (println "Bucket name = " s3-bucket-name)
  (let [concept-id (get-in event [:path-parameters :concept_id])
        height (or (get-in event [:query-string-parameters :h]) "300")
        width (or (get-in event [:query-string-parameters :w]) "300")
        index (or (get-in event [:query-string-parameters :n]) "0")
        scaled-url (try (core/resize-image-from-concept concept-id height width)
                    (catch Exception e "https://s3.amazonaws.com/hackfest2scaler/image-unavailable.svg"))
        response {"statusCode" 301
                   :headers {:location scaled-url}
                   :body (str "Image available at " scaled-url)}]
    response))

(ns browse-scaler.test-runner
 (:require [doo.runner :refer-macros [doo-tests]]
           [browse-scaler.core-test]
           [cljs.nodejs :as nodejs]))

(try
  (.install (nodejs/require "source-map-support"))
  (catch :default _))

(doo-tests
 'browse-scaler.core-test)

(defproject browse-scaler "0.1.0-SNAPSHOT"
  :description "A small service that scales browse-images of given NASA Earth Science
                metadata records."
  :url "http://earthdata.nasa.gov"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [org.clojure/core.async "0.3.442"]
                 [cljs-http "0.1.45"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [funcool/promesa "1.9.0"]]
  :plugins [[lein-npm "0.6.2"]
            [lein-cljsbuild "1.1.7"]]
  :npm {:dependencies [[source-map-support "0.5.3"]
                       [aws-sdk "2.188.0"]
                       [sharp "0.20.0"]]}
  :source-paths ["src"]
  :cljs-lambda
   {:defaults      {:role "FIXME"}
    :resource-dirs ["static"]
    :functions
    [{:name   "scale"
      :invoke browse-scaler.core/handle-event}]}
  :cljsbuild
   {:repl-listen-port 9000
    :builds [{:id "lambda-build"
              :source-paths ["src"]
              :compiler {:output-to     "app/main.js"
                         :output-dir    "app"
                         :main          "browse-scaler.handler"
                         :source-map    true
                         :target        :nodejs
                         :language-in   :ecmascript6
                         :optimizations :none}}]}

  :aliases {
            "node-repl"
            ^{:doc "Start a Node.js-based Clojurescript REPL"}
            ["trampoline" "run" "-m" "clojure.main"
             "repl/node_repl.clj"]})

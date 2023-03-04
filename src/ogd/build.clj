(ns ogd.build
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [dtv]
            [graph]
            [nextjournal.clerk :as clerk]
            [nextjournal.clerk.config :as clerk.config]
            [nextjournal.clerk.view :as view]))

(defn gen-graph! [name data]
  (let [out-path (str "build/"
                      (-> name
                          (str/replace #"\s" "")
                          (str/replace #"/" "-")
                          str/lower-case))
        out-name (str "public/" out-path ".html")
        notebook "notebooks/graph.clj"]
    (fs/create-dirs (fs/parent out-name))
    (reset! graph/data data)
    (spit out-name
          (view/->static-app
           {:bundle? false
            :current-path notebook
            :path->url {notebook out-name
                        "" "index.html"}
            :path->doc (hash-map notebook (clerk/file->viewer notebook))
            :resource->url @clerk.config/!resource->url}))))


(defn all! [_opts]
  (doseq [[name data] dtv/counting-points]
    (gen-graph! name data)))


(comment
  (all! {}))

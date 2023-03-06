(ns ogd.build
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [dtv]
            [graph]
            [nextjournal.clerk :as clerk]
            [nextjournal.clerk.config :as clerk.config]
            [nextjournal.clerk.view :as view]
            [rewrite-clj.zip :as z]))

(defn gen-graph! [name data]
  (let [out-path (str "build/"
                      )
        out-name (str "public/" out-path ".html")
        notebook "notebooks/graph.clj"]
    (fs/create-dirs (fs/parent out-name))
    (reset! graph/data data)
    (spit out-name
          (view/->static-app
           {:bundle?       false
            :current-path  notebook
            :path->url     {notebook out-name
                            ""       "index.html"}
            :path->doc     {notebook (clerk/file->viewer notebook)}
            :resource->url @clerk.config/!resource->url}))))


(defn all! [_args]
  (doseq [[name data] dtv/graph-maps]
    (gen-graph! name data)))

(defn id->file-name [id]
  (str "notebooks/dtv/zählstelle-"
       (-> id
           (str/replace #"\s" "")
           (str/replace #"/" "-")
           str/lower-case)
       ".clj"))

(defn notebook-contents [id]
  (->
   (z/of-file "notebooks/graph.clj")
   (z/find-value z/next 'counting-point-label)
   (z/right)
   (z/replace id)
   (z/root-string)))

(defn gen-counting-points-notebooks! []
  (fs/create-dirs "notebooks/dtv")
  (doseq [id (keys dtv/graph-maps)]
    (let [out-name (id->file-name id)
          contents (notebook-contents id)]
      (spit out-name contents))))

(defn all-for-garden! [_args]
  (gen-counting-points-notebooks!)
  (nextjournal.clerk/build! {:paths ["notebooks/dtv/zählstelle-*.clj"]
                             :index "notebooks/dtv_index.clj"}))

(comment
  (gen-counting-points-notebooks!)

  (all-for-garden! {})
  (all! {}))

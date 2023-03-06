(ns ogd.build
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [dtv]
            [graph]
            [nextjournal.clerk :as clerk]
            [rewrite-clj.zip :as z]))

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

(defn all-for-garden! [args]
  (gen-counting-points-notebooks!)
  (nextjournal.clerk/build! (merge args
                                   {:paths ["notebooks/dtv/zählstelle-*.clj"]
                                    :index "notebooks/dtv_index.clj"})))

(comment
  (gen-counting-points-notebooks!)
  (all-for-garden! {}))

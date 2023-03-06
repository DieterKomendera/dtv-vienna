(require
 '[nextjournal.clerk :as clerk])

(comment
  (clerk/serve! {:browse? true
                 :port 7776})

  (clerk/show! "notebooks/dtv.clj")
  (clerk/show! "notebooks/graph.clj")

  (clerk/build! {:paths ["notebooks/dtv.clj"]
                 :bundle? true}))

;; # DTV Index 🗂

^{:nextjournal.clerk/visibility :hide}
(ns ^:nextjournal.clerk/no-cache dtv-index
  (:require [dtv]
            [ogd.build]
            [nextjournal.clerk :as clerk]))

(clerk/html
 [:div.viewer-markdown
  (into [:ul]
        (map
         (fn [id]
           [:li [:a.underline {:href (clerk/doc-url (ogd.build/id->file-name id))} id]])
         (sort (keys dtv/graph-maps))))])

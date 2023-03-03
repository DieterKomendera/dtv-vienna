^{:nextjournal.clerk/visibility :hide}
(ns index
  (:require [clojure.string :as str]
            [dtv :as dtv]
            [nextjournal.clerk :as clerk])
  (:import [java.time LocalDate]))

;;# Übersicht Dauerzählstellen der Stadt Wien
^{::clerk/viewer clerk/hide-result}
(defn name->path [n]
  [:a {:href (str (-> n
                      (str/replace #"\s" "")
                      (str/replace #"/" "-")
                      str/lower-case)
                  ".html")}
   n])


(clerk/html
 (into [:ul.font-sans]
       (comp
        (map name->path)
        (map (fn [f] [:li f])))
       (sort (keys dtv/counting-points))))


(clerk/html
 [:div.text-xs.font-mono.text-slate-500
  [:p
   "Datenquelle: Stadt Wien – "
   [:a {:href "https://data.wien.gv.at"} "data.wien.gv.at"] " | "
   "Datensatz: "
   [:a {:href "https://www.data.gv.at/katalog/dataset/4707e82a-154f-48b2-864c-89fffc6334e1" }
    "Verkehrszählstellen Zählwerte Wien"]]
  [:p "Erstellt am " (str (LocalDate/now)) " von " [:a.text-blue.underline {:href "https://twitter.com/DieterKomendera"} "Dieter Komendera"]]])

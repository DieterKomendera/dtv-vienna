;; # DTV Wien

^{:nextjournal.clerk/visibility :hide}
(ns ^:nextjournal.clerk/no-cache dtv-index
  (:require [dtv]
            [ogd.build]
            [nextjournal.clerk :as clerk])
  (:import [java.time LocalDate]))

(clerk/html
 [:div.text-xs.font-mono
  [:p "Eine Auswertung des DTV (Durchschnittlicher Täglicher Verkehr) der Dauerzählstellen der Stadt Wien basierend auf den durch die MA46 bereitgestellten Daten. Ausgewertet wurde der Wert " [:code "DTVMF"] " (KFZ pro 24h an Werktagen)."]])

(clerk/html
 [:div.text-xs.font-mono.text-slate-500
  [:p
   "Datenquelle: Stadt Wien – "
   [:a {:href "https://data.wien.gv.at"} "data.wien.gv.at"] " | "
   "Datensatz: "
   [:a {:href "https://www.data.gv.at/katalog/dataset/4707e82a-154f-48b2-864c-89fffc6334e1" }
    "Verkehrszählstellen Zählwerte Wien"]]])

(clerk/html
 [:div.viewer-markdown.text-xs
  (into [:ul]
        (map
         (fn [id]
           [:li [:a.underline {:href (clerk/doc-url (ogd.build/id->file-name id))} id]])
         (sort (keys dtv/graph-maps))))])

(clerk/html
 [:div.text-xs.font-mono.text-slate-500
  [:p "Erstellt am " (str (LocalDate/now)) " von " [:a.text-blue.underline {:href "https://wien.rocks/DieterKomendera"} "Dieter Komendera"]]])

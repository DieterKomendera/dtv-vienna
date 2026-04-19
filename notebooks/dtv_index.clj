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
 [:div.text-xs.font-mono
  [:p "Die Detailseiten wurden verschoben zu: "
   [:a.text-blue.underline {:href "https://ogd-wien.apps.garden/dtv"} "ogd-wien.apps.garden/dtv"]]
  [:p.text-slate-500 "Die Detailseiten wurden verschoben."]])

(clerk/html
 [:div.text-xs.font-mono.text-slate-500
  [:p "Erstellt am " (str (LocalDate/now)) " von " [:a.text-blue.underline {:href "https://wien.rocks/DieterKomendera"} "Dieter Komendera"]]])

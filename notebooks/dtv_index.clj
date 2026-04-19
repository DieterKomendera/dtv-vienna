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
 [:div.text-xs.font-mono
  [:p "Die Detailseiten wurden verschoben zu: "
   [:a.text-blue.underline {:href "https://ogd-wien.apps.garden/dtv"} "ogd-wien.apps.garden/dtv"]]
  [:p.text-slate-500 "Sie werden in " [:span#countdown "5"] " Sekunden automatisch weitergeleitet..."]])

(clerk/html
 [:script {:type "text/javascript"}
  "
  let seconds = 5;
  const countdownEl = document.getElementById('countdown');
  const interval = setInterval(() => {
    seconds--;
    if (countdownEl) countdownEl.textContent = seconds;
    if (seconds <= 0) {
      clearInterval(interval);
      window.location.href = 'https://ogd-wien.apps.garden/dtv';
    }
  }, 1000);
  "])

(clerk/html
 [:div.text-xs.font-mono.text-slate-500
  [:p "Erstellt am " (str (LocalDate/now)) " von " [:a.text-blue.underline {:href "https://wien.rocks/DieterKomendera"} "Dieter Komendera"]]])

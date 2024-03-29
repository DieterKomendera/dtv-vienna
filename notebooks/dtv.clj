;; # DTV - Durchschnittlicher täglicher Verkehr

^{:nextjournal.clerk/visibility :hide-ns}
(ns dtv
  (:require [nextjournal.clerk :as clerk]
            [tablecloth.api :as tc]
            [ogd.utils :as utils]
            [tech.v3.dataset :as ds]
            [tech.v3.dataset.join :as ds-join]))

(def data-url
  "https://www.wien.gv.at/verkehr/verkehrsmanagement/ogd/dauerzaehlstellen.csv")

;; Quelle: https://www.data.gv.at/katalog/dataset/4707e82a-154f-48b2-864c-89fffc6334e1

^::clerk/no-cache
(def counting-points-ds
  (ds/->dataset
   (utils/string->stream (slurp data-url :encoding "ISO-8859-1"))
   {:separator \;
    :file-type :csv
    :parser-fn {"MONAT" [:int8 utils/month-str->int]}}))

(keys (ds/head counting-points-ds))

;; ```
;; JAHR = Zähljahr
;; MONAT = Zählmonat
;; ZNR = Zählstellenummer
;; ZNAME = Zählstellenname
;; STRTYP = Straßentyp (B = Hauptstraße B, G = Gemeindestraße)
;; STRNR = Straßennummer (Bezeichnung des Hauptstraßen B-Netzes)
;; RINAME = Richtungsname (Fahrziel)
;; FZTYP = Fahrzeugtyp / -gruppe (Kfz = Alle Kraftfahrzeuge, LkwÄ = Lkw-ähnliche Kraftfahrzeuge)
;; DTV = Durchschnittlicher täglicher Verkehr (Anzahl der Fahrzeuge laut FZTYP pro 24h)
;; DTVMS = Montag bis Sonntag (alle Tage)
;; DTVMF = Montag bis Freitag (keine Feiertage)
;; DTVMO = Montag (keine Feiertage)
;; DTVDD = Dienstag bis Donnerstag (keine Feiertage)
;; DTVFR = Freitag (keine Feiertage)
;; DTVSA = Samstag (keine Feiertage)
;; DTVSF = Sonn- und Feiertage
;; TVMAX = Maximaler Tagesverkehr (alle Tage)
;; TVMAXT = Wochentag und Datum des TVMAX, * =Tag enthält geschätzte Werte -29 = Negative Werte kennzeichnen nicht verfügbare Werte (z.B. Ausfälle oder Gegenrichtung in Einbahnstraßen)
;; ```


(def locations-url "https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&srsName=EPSG:4326&outputFormat=csv&typeName=ogdwien:DAUERZAEHLOGD")

(def locations-ds
  (ds/->dataset
   (utils/string->stream (slurp locations-url :encoding "ISO-8859-1"))
   {:separator \,
    :file-type :csv
    :parser-fn {"SHAPE" [:object utils/point->latlng]}}))


(def dtv-graph
  {:width 650
   :height 350
   :transform [{:calculate "datetime(datum.JAHR, datum.MONAT - 1, 1)" :as "m-jahr"}]
   :layer [{:mark {:type "bar" :tooltip true}
            :encoding {:x {:field "m-jahr"
                           :title "Monat"
                           :type "temporal"
                           :timeUnit "yearmonth"
                           :axis {:labelAngle -45
                                  :labelFlush true
                                  :tickCount {:interval "month" :step 3}}}
                       :y {:field "DTVMF"
                           :title "Durchschnittlicher täglicher Verkehr (KFZ pro 24h an Werktagen)"
                           :aggregate "sum"}}}
           {:mark {:type "line"
                   :color "firebrick"}
            :transform [{:loess "DTVMF"
                         :on "m-jahr"}]
            :encoding {:x {:field "m-jahr"
                           :title "Monat"
                           :type "temporal"
                           :timeUnit "yearmonth"}
                       :y {:field "DTVMF"
                           :aggregate "sum"}}}]
   :config {:view {:stroke :transparent}
            :axis {:domainWidth 1} }})



(def counting-points
  (-> (ds-join/left-join ["ZNR" "ZST_ID"] counting-points-ds locations-ds)
      ;; all cars, not only LkwÄ
      (ds/filter-column "FZTYP" #(= "Kfz" %))
      ;; only consider rows which have a meaningful value
      (ds/filter-column "DTVMF" #(< 0 %))
      ;; group rows by counting point
      (tc/group-by ["ZNAME" "RINAME" "ZNR"])
      ;; filter grouped datasets for only ones with recent data
      (tc/without-grouping->
       (tc/drop-rows (fn [r] (> 2022 (apply max (get (:data r) "JAHR"))))))
      (tc/groups->map)))

(defn group-cols->label [{:as _group-cols :strs [ZNAME RINAME ZNR]}]
  (str ZNAME " - " RINAME " - " ZNR))

(defn counting-point-data->vl-graph [{:as _group-cols :strs [ZNAME RINAME ZNR]} ds]
  (-> dtv-graph
      (assoc :dtv/location (first (get ds "SHAPE")))
      (assoc :title {:text     (str ZNAME " - " RINAME)
                     :subtitle (str "Zählstelle Nr. " ZNR " in Richtung " RINAME)})
      (assoc-in [:data :values] (into [] (ds/mapseq-reader ds)))))

(def graph-maps
  (->> counting-points
       (into {}
             (map (fn [[group-cols ds]]
                    [(group-cols->label group-cols)
                     (counting-point-data->vl-graph group-cols ds)])))))

(defonce select-state (atom (first (sort (keys graph-maps)))))

(clerk/with-viewer
  {:transform-fn (comp (clerk/update-val
                        (fn [var]
                          {:var-name (symbol var)
                           :value @@var
                           :options (sort (keys graph-maps)) }))
                       clerk/mark-presented)

   :render-fn    '(fn [{:as x :keys [var-name value options]}]
                    (nextjournal.clerk.viewer/html
                     (into
                      [:select {:on-change #(nextjournal.clerk.viewer/clerk-eval
                                             `(reset! ~var-name ~(.. % -target -value)))}]
                      (map (fn [n] [:option n]) options))))}
  #'select-state)

^::clerk/no-cache
(when-let [data (get graph-maps @select-state)]
  (clerk/vl data))

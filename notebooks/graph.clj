^{:nextjournal.clerk/visibility :hide}
(ns graph
  (:require [dtv]
            [nextjournal.clerk :as clerk])
  (:import [java.time LocalDate]))

^{::clerk/visibility {:result :hide}}
(def counting-point-label "Burggasse II - Zentrum - 1213")

^{::clerk/visibility {:result :hide}}
(def data (get dtv/graph-maps counting-point-label))

(clerk/html
 [:h1 (:text (:title data))])

(clerk/vl data)

(clerk/html
 [:link {:rel "stylesheet"
         :href "https://unpkg.com/leaflet@1.9.3/dist/leaflet.css"
         :crossorigin ""}])

^{::clerk/visibility {:result :hide}}
(def leaflet
  {:transform-fn clerk/mark-presented
   :render-fn
   '(fn [value]
      (nextjournal.clerk.viewer/html
       (when-let [{:keys [lat lng]} value]
         [nextjournal.clerk.viewer/with-d3-require {:package ["leaflet@1.9.3/dist/leaflet.js"]}
          (fn [leaflet]
            [:div {:style {:height 400}
                   :ref
                   (fn [el]
                     (when el
                       (when-let [m (.-map el)] (.remove m))
                       (let [m                   (.map leaflet el (clj->js {:zoomControl true :zoomDelta 0.5 :zoomSnap 0.0 :attributionControl false}))
                             location-latlng     (.latLng leaflet lat lng)
                             location-marker     (.marker leaflet location-latlng)
                             basemap-hidpi-layer (.tileLayer leaflet
                                                             "https://mapsneu.wien.gv.at/basemap/bmaphidpi/normal/google3857/{z}/{y}/{x}.jpeg"
                                                             (clj->js
                                                              {:maxZoom       25
                                                               :maxNativeZoom 19
                                                               :attribution   "basemap.at"
                                                               :errorTileUrl  "/transparent.gif"}))]
                         (set! (.-map el) m)
                         (.addTo basemap-hidpi-layer m)
                         (.addTo location-marker m)
                         (.setView m location-latlng 13.7))))}])])))})


;; ## Zählpunkt Standort
(clerk/with-viewer leaflet
  (:dtv/location data))

(let [{:keys [dtv/location]} data]
  (clerk/html
   [:div.text-xs.font-mono.text-slate-500
    [:span (:lat location) ", " (:lng location) " | "]
    [:a
     {:href (format "https://www.google.com/maps/@?api=1&map_action=pano&viewpoint=%s,%s&heading=117&pitch=10&fov=250" (:lat location) (:lng location))
      :target "_blank"}
     "Auf Google Street View anzeigen"]]))

^::clerk/no-cache
(clerk/html
 [:div.text-xs.font-mono.text-slate-500
  [:p
   "Karte:"
   [:a {:href "https://basemap.at"} "basemap.at"] " | "
   "Datenquelle: Stadt Wien – "
   [:a {:href "https://data.wien.gv.at"} "data.wien.gv.at"] " | "
   "Datensatz: "
   [:a {:href "https://www.data.gv.at/katalog/dataset/4707e82a-154f-48b2-864c-89fffc6334e1" }
    "Verkehrszählstellen Zählwerte Wien"]]
  [:p "Erstellt am " (str (LocalDate/now)) " von " [:a.text-blue.underline {:href "https://wien.rocks/@DieterKomendera"} "Dieter Komendera"]]])

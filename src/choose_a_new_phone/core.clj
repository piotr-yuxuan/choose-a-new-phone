(ns choose-a-new-phone.core
  (:require [cognitect.transit :as transit]
            [choose-a-new-phone.events.lineage-wiki :as lineage-wiki]
            [choose-a-new-phone.events.db :as events.db]
            [clojure.java.io :as io]
            [clj-time.coerce :as c]
            [hiccup.page :refer [include-js include-css html5]]
            [re-frame.core :as re-frame]
            [clojure.string :as str])
  (:import (com.cognitect.transit WriteHandler))
  (:gen-class))

(def dehydrated-db-file
  "dehydrated-db.json")

(def DateTimeHandler
  (reify WriteHandler
    (tag [this v] "datetime")
    (rep [this v] (c/to-long v))
    (stringRep [this v] nil)
    (getVerboseHandler [_] nil)))

(comment
  (re-frame/dispatch [::events.db/initialize-db])
  (re-frame/dispatch [::lineage-wiki/get-phone-list])
  (:pending-phone-request @re-frame.db/app-db)

  (with-open [output-stream (io/output-stream dehydrated-db-file)]
    (let [writer (transit/writer output-stream
                                 :json
                                 {:handlers {org.joda.time.DateTime DateTimeHandler}})]
      (transit/write writer @re-frame.db/app-db)))
  )

(defmacro dehydrated-default-db
  []
  (slurp dehydrated-db-file))

(defn -main
  ""
  [& args])

(def headers
  {"Accept" "application/json, text/plain, */*"
   "Accept-Language" "en-US,en;q=0.5"
   "Connection" "keep-alive"
   "Pragma" "no-cache"
   "Cache-Control" "no-cache"})

(defn fetch-price-hint
  "To be implemented"
  [release-year plausible-name] ;; should I add the vendor?
  nil)

(defn dumb-plausible-name
  [phone-name]
  (let [truncate-index (or (str/index-of phone-name "(")
                           (str/index-of phone-name "[")
                           (str/index-of phone-name "/")
                           (count phone-name))]
    (subs (str phone-name) 0 truncate-index)))

(defn assoc-price-hint
  [phone]
  (let [fetched-price-hint (fetch-price-hint (:release-year phone)
                                             (-> phone :name dumb-plausible-name))
        maybe-price-hint (when-let [price-hint (and fetched-price-hint
                                                    (->> fetched-price-hint (re-seq #"\d+") first))]
                           (Integer. price-hint))]
    (assoc phone :price-hint maybe-price-hint)))

(defn price-hints
  [phones]
  (->> phones
       (pmap assoc-price-hint)
       (sort-by :name)
       seq))

(def hard-coded-price-hints
  [{:name "10", :release-year "2016", :price-hint 210}
   {:name "2", :release-year "2015", :price-hint 500}
   {:name "3 / 3T", :release-year "2016", :price-hint 210}
   {:name "5", :release-year "2017", :price-hint 530}
   {:name "5T", :release-year "2017", :price-hint 620}
   {:name "Android One 2nd Gen", :release-year "2015"}
   {:name "Aquaris E5 4G / Aquaris E5s", :release-year "2015"}
   {:name "Aquaris M5", :release-year "2015", :price-hint 260}
   {:name "Aquaris U", :release-year "2016", :price-hint 190}
   {:name "Aquaris U Plus", :release-year "2016", :price-hint 220}
   {:name "Aquaris X5", :release-year "2015", :price-hint 230}
   {:name "Aquaris X5 Plus", :release-year "2016", :price-hint 300}
   {:name "Ascend Mate 2 4G", :release-year "2014"}
   {:name "Axon 7", :release-year "2016", :price-hint 410}
   {:name "Axon 7 Mini", :release-year "2016", :price-hint 270}
   {:name "Benefit A3", :release-year "2015"}
   {:name "Droid 4", :release-year "2012", :price-hint 190}
   {:name "Droid Bionic", :release-year "2011", :price-hint 210}
   {:name "Droid RAZR/RAZR MAXX (CDMA)", :release-year "2011"}
   {:name "FP2", :release-year "2015"}
   {:name "Find 7a/s", :release-year "2014", :price-hint 370}
   {:name "G Pad 7.0 (LTE)", :release-year "2014"}
   {:name "G Pad 7.0 WiFi", :release-year "2014"}
   {:name "G Pad 8.0 (Wi-Fi)", :release-year "2014"}
   {:name "G Pad 8.3", :release-year "2013"}
   {:name "G Pad X (T-Mobile)", :release-year "2016"}
   {:name "G2 (AT&T)", :release-year "2013", :price-hint 100}
   {:name "G2 (Canadian)", :release-year "2013", :price-hint 100}
   {:name "G2 (International)", :release-year "2013", :price-hint 100}
   {:name "G2 (T-Mobile)", :release-year "2013", :price-hint 100}
   {:name "G2 Mini", :release-year "2014", :price-hint 160}
   {:name "G3 (AT&T)", :release-year "2014", :price-hint 240}
   {:name "G3 (Canada)", :release-year "2014", :price-hint 240}
   {:name "G3 (International)", :release-year "2014", :price-hint 240}
   {:name "G3 (Korea)", :release-year "2014", :price-hint 240}
   {:name "G3 (Sprint)", :release-year "2014", :price-hint 240}
   {:name "G3 (T-Mobile)", :release-year "2014", :price-hint 240}
   {:name "G3 (Verizon)", :release-year "2014", :price-hint 240}
   {:name "G3 Beat", :release-year "2014", :price-hint 190}
   {:name "G3 S", :release-year "2014", :price-hint 240}
   {:name "G4 (International)", :release-year "2015", :price-hint 250}
   {:name "G4 (T-Mobile)", :release-year "2015", :price-hint 250}
   {:name "G5 (International)", :release-year "2016", :price-hint 300}
   {:name "G5 (T-Mobile)", :release-year "2016", :price-hint 300}
   {:name "G6 (EU Unlocked)", :release-year "2017"}
   {:name "G6 (US Unlocked)", :release-year "2017"}
   {:name "Galaxy A5 (2017)", :release-year "2017", :price-hint 270}
   {:name "Galaxy A7 (2017)", :release-year "2017", :price-hint 390}
   {:name "Galaxy Nexus GSM", :release-year "2011"}
   {:name "Galaxy Nexus LTE (Sprint)", :release-year "2012", :price-hint 270}
   {:name "Galaxy Nexus LTE (Verizon)", :release-year "2011"}
   {:name "Galaxy Note 10.1 2014 (LTE)", :release-year "2013"}
   {:name "Galaxy Note 10.1 Wi-Fi (2014)", :release-year "2013"}
   {:name "Galaxy Note 2 (LTE)", :release-year "2012"}
   {:name "Galaxy Note 3 (International 3G)", :release-year "2013", :price-hint 420}
   {:name "Galaxy Note 3 LTE (N9005/P)", :release-year "2013"}
   {:name "Galaxy Note 3 LTE (N900T/V/W8)", :release-year "2013"}
   {:name "Galaxy Note 8.0 (GSM)", :release-year "2013"}
   {:name "Galaxy Note 8.0 (LTE)", :release-year "2013"}
   {:name "Galaxy Note 8.0 (Wi-Fi)", :release-year "2013"}
   {:name "Galaxy Note Pro 12.2 Wi-Fi", :release-year "2014"}
   {:name "Galaxy S II", :release-year "2011", :price-hint 170}
   {:name "Galaxy S III (AT&T)", :release-year "2012", :price-hint 130}
   {:name "Galaxy S III (International)", :release-year "2012", :price-hint 130}
   {:name "Galaxy S III (LTE / International)", :release-year "2012", :price-hint 130}
   {:name "Galaxy S III (Sprint)", :release-year "2012", :price-hint 130}
   {:name "Galaxy S III (T-Mobile)", :release-year "2012", :price-hint 130}
   {:name "Galaxy S III (Verizon)", :release-year "2012", :price-hint 130}
   {:name "Galaxy S4 (GT-I9505/G, SGH-M919)", :release-year "2013", :price-hint 230}
   {:name "Galaxy S4 (Verizon)", :release-year "2013", :price-hint 230}
   {:name "Galaxy S4 LTE-A (GT-I9506)", :release-year "2013", :price-hint 390}
   {:name "Galaxy S4 Mini (International 3G)", :release-year "2013", :price-hint 230}
   {:name "Galaxy S4 Mini (International Dual SIM)", :release-year "2013", :price-hint 230}
   {:name "Galaxy S4 Mini (International LTE)", :release-year "2013", :price-hint 230}
   {:name "Galaxy S5 (International 3G)", :release-year "2014", :price-hint 220}
   {:name "Galaxy S5 Active", :release-year "2014", :price-hint 400}
   {:name "Galaxy S5 LTE (G9006V/8V)", :release-year "2014"}
   {:name "Galaxy S5 LTE (G900AZ/F/M/R4/R7/T/V/W8,S902L)", :release-year "2014"}
   {:name "Galaxy S5 LTE (G900I/P)", :release-year "2014"}
   {:name "Galaxy S5 LTE (G900K/L/S)", :release-year "2014"}
   {:name "Galaxy S5 LTE (SCL23)", :release-year "2014"}
   {:name "Galaxy S5 LTE Duos (G9006W/8W)", :release-year "2014"}
   {:name "Galaxy S5 LTE Duos (G900FD/MD)", :release-year "2014"}
   {:name "Galaxy S5 LTE-A", :release-year "2014", :price-hint 300}
   {:name "Galaxy S5 Plus", :release-year "2014", :price-hint 380}
   {:name "Galaxy S5 Sport", :release-year "2014", :price-hint 300}
   {:name "Galaxy S6", :release-year "2015", :price-hint 280}
   {:name "Galaxy S6 Edge", :release-year "2015", :price-hint 420}
   {:name "Galaxy S7", :release-year "2016", :price-hint 450}
   {:name "Galaxy S7 Edge", :release-year "2016", :price-hint 450}
   {:name "Galaxy S9", :release-year "2018", :price-hint 990}
   {:name "Galaxy S9+", :release-year "2018", :price-hint 990}
   {:name "Galaxy Tab 2 7.0 / Tab 2 10.1 (GSM)", :release-year "2012"}
   {:name "Galaxy Tab 2 7.0 / Tab 2 10.1 (Wi-Fi / Wi-Fi + IR)", :release-year "2012"}
   {:name "Galaxy Tab 3 7.0 LTE", :release-year "2016"}
   {:name "Galaxy Tab E 8.0 LTE (Sprint)", :release-year "2016"}
   {:name "Galaxy Tab E 9.6 (WiFi)", :release-year "2015"}
   {:name "Galaxy Tab PRO 10.1", :release-year "2014"}
   {:name "Galaxy Tab Pro 8.4", :release-year "2014"}
   {:name "Galaxy Tab S 10.5 Wi-Fi", :release-year "2014"}
   {:name "Galaxy Tab S 8.4 Wi-Fi", :release-year "2014"}
   {:name "Galaxy Tab S2 8.0 Wi-Fi (2016)", :release-year "2016"}
   {:name "Galaxy Tab S2 9.7 (LTE)", :release-year "2015"}
   {:name "Galaxy Tab S2 9.7 (Wi-Fi)", :release-year "2015"}
   {:name "Galaxy Tab S2 9.7 Wi-Fi (2016)", :release-year "2016"}
   {:name "Honor 4/4X (Unified)", :release-year "2014"}
   {:name "Honor 4x (China Telecom)", :release-year "2014"}
   {:name "Honor 5X", :release-year "2015", :price-hint 180}
   {:name "K10", :release-year "2016", :price-hint 170}
   {:name "L90", :release-year "2014", :price-hint 170}
   {:name "Le 2", :release-year "2016", :price-hint 190}
   {:name "Le Max2", :release-year "2016"}
   {:name "Le Pro3 / Le Pro3 Elite", :release-year "2017", :price-hint 280}
   {:name "Mi 3 / Mi 4", :release-year "2014"}
   {:name "Mi 4c", :release-year "2015", :price-hint 210}
   {:name "Mi 5", :release-year "2016", :price-hint 240}
   {:name "Mi 5s", :release-year "2016", :price-hint 250}
   {:name "Mi 5s Plus", :release-year "2016", :price-hint 310}
   {:name "Mi 6", :release-year "2017", :price-hint 330}
   {:name "Mi A1", :release-year "2017", :price-hint 170}
   {:name "Mi MIX", :release-year "2016", :price-hint 390}
   {:name "Mi MIX 2", :release-year "2017", :price-hint 410}
   {:name "Mi Max", :release-year "2016", :price-hint 150}
   {:name "Mi Note 2", :release-year "2016", :price-hint 310}
   {:name "Moto E", :release-year "2014", :price-hint 100}
   {:name "Moto E 2015", :release-year "2015"}
   {:name "Moto E 2015 LTE", :release-year "2015"}
   {:name "Moto G", :release-year "2013", :price-hint 130}
   {:name "Moto G 2014", :release-year "2014"}
   {:name "Moto G 2014 LTE", :release-year "2015"}
   {:name "Moto G 2015", :release-year "2015"}
   {:name "Moto G 4G", :release-year "2014", :price-hint 160}
   {:name "Moto G3 Turbo", :release-year "2015"}
   {:name "Moto G4", :release-year "2016", :price-hint 160}
   {:name "Moto G4 Play", :release-year "2016", :price-hint 130}
   {:name "Moto X", :release-year "2013", :price-hint 250}
   {:name "Moto X 2014", :release-year "2014"}
   {:name "Moto X Play", :release-year "2015", :price-hint 240}
   {:name "Moto X Pure Edition/Style (2015)", :release-year "2015", :price-hint 260}
   {:name "Moto Z", :release-year "2016", :price-hint 290}
   {:name "Moto Z Play", :release-year "2016", :price-hint 290}
   {:name "Moto Z2 Force", :release-year "2017", :price-hint 630}
   {:name "N3", :release-year "2015"}
   {:name "Nexus 10", :release-year "2012"}
   {:name "Nexus 4", :release-year "2012", :price-hint 250}
   {:name "Nexus 5", :release-year "2013", :price-hint 260}
   {:name "Nexus 5X", :release-year "2015", :price-hint 270}
   {:name "Nexus 6", :release-year "2014", :price-hint 420}
   {:name "Nexus 6P", :release-year "2015", :price-hint 300}
   {:name "Nexus 7 (LTE, 2013 version)", :release-year "2013"}
   {:name "Nexus 7 (Wi-Fi, 2013 version)", :release-year "2013"}
   {:name "Nexus 9 (LTE)", :release-year "2014"}
   {:name "Nexus 9 (Wi-Fi)", :release-year "2014"}
   {:name "Nexus Player", :release-year "2014"}
   {:name "One", :release-year "2014", :price-hint 360}
   {:name "One (GSM)", :release-year "2013", :price-hint 330}
   {:name "One (M8)", :release-year "2014", :price-hint 360}
   {:name "One (M8) Dual SIM", :release-year "2014", :price-hint 360}
   {:name "One (Verizon)", :release-year "2013", :price-hint 330}
   {:name "One A9", :release-year "2015", :price-hint 300}
   {:name "One M9 (GSM)", :release-year "2015", :price-hint 300}
   {:name "One M9 (Verizon)", :release-year "2015", :price-hint 300}
   {:name "One Max (GSM)", :release-year "2013", :price-hint 370}
   {:name "One Max (Verizon)", :release-year "2013", :price-hint 370}
   {:name "Optimus L70", :release-year "2014"}
   {:name "P2", :release-year "2016", :price-hint 170}
   {:name "Photon Q 4G LTE", :release-year "2012", :price-hint 220}
   {:name "Pixel C", :release-year "2015"}
   {:name "R5/R5s (International)", :release-year "2015"}
   {:name "R7 Plus (International)", :release-year "2015"}
   {:name "R7s (International)", :release-year "2015"}
   {:name "RAZR/RAZR MAXX (GSM)", :release-year "2011"}
   {:name "Redmi 1S", :release-year "2014", :price-hint 90}
   {:name "Redmi 2", :release-year "2015", :price-hint 130}
   {:name "Redmi 3/Prime", :release-year "2016", :price-hint 120}
   {:name "Redmi 3S/3X", :release-year "2016", :price-hint 120}
   {:name "Redmi Note 3", :release-year "2016", :price-hint 160}
   {:name "Redmi Note 4", :release-year "2017", :price-hint 150}
   {:name "Redmi Note 5 Pro", :release-year "2018", :price-hint 200}
   {:name "Robin", :release-year "2016"}
   {:name "Shield Android TV", :release-year "2015"}
   {:name "Shield Portable", :release-year "2013"}
   {:name "Shield Tablet", :release-year "2014"}
   {:name "Storm", :release-year "2015"}
   {:name "Swift", :release-year "2015"}
   {:name "V20 (AT&T)", :release-year "2016", :price-hint 460}
   {:name "V20 (Sprint)", :release-year "2016", :price-hint 460}
   {:name "V20 (T-Mobile)", :release-year "2016", :price-hint 460}
   {:name "V20 (US Unlocked)", :release-year "2016", :price-hint 460}
   {:name "V20 (Verizon)", :release-year "2016", :price-hint 460}
   {:name "Vibe K5 / K5 Plus", :release-year "2016", :price-hint 140}
   {:name "Vibe Z2 Pro", :release-year "2014", :price-hint 430}
   {:name "X", :release-year "2015", :price-hint 500}
   {:name "Xperia L", :release-year "2013", :price-hint 130}
   {:name "Xperia M", :release-year "2013", :price-hint 130}
   {:name "Xperia SP", :release-year "2013", :price-hint 180}
   {:name "Xperia Tablet Z LTE", :release-year "2013"}
   {:name "Xperia Tablet Z Wi-Fi", :release-year "2013"}
   {:name "Xperia XA2", :release-year "2018"}
   {:name "Xperia Z", :release-year "2013", :price-hint 330}
   {:name "Xperia Z3+", :release-year "2015", :price-hint 290}
   {:name "Xperia Z4 Tablet LTE", :release-year "2015"}
   {:name "Xperia Z4 Tablet WiFi", :release-year "2015"}
   {:name "Xperia Z5", :release-year "2015", :price-hint 270}
   {:name "Xperia Z5 Compact", :release-year "2015", :price-hint 390}
   {:name "Xperia ZL", :release-year "2013", :price-hint 260}
   {:name "Xperia ZR", :release-year "2013", :price-hint 230}
   {:name "Yunique", :release-year "2015", :price-hint 70}
   {:name "Yuphoria", :release-year "2015", :price-hint 90}
   {:name "Yureka / Yureka Plus", :release-year "2015", :price-hint 130}
   {:name "Z1", :release-year "2015", :price-hint 320}
   {:name "Z9 Max", :release-year "2015", :price-hint 200}
   {:name "ZenPad 8.0 (Z380KL)", :release-year "2015"}
   {:name "Zenfone 2 (1080p)", :release-year "2015", :price-hint 300}
   {:name "Zenfone 2 (720p)", :release-year "2015", :price-hint 300}
   {:name "Zenfone 2 (ZE500CL)", :release-year "2015", :price-hint 300}
   {:name "Zenfone 2 Laser (720p)", :release-year "2015", :price-hint 210}
   {:name "Zenfone 2 Laser/Selfie (1080p)", :release-year "2015", :price-hint 210}])

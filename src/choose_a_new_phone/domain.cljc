(ns choose-a-new-phone.domain
  "Domain-specific knowledge"
  #?(:clj  (:require [clj-time.format :as f]
                     [clj-time.coerce :as c]
                     [clj-time.core :as t])
     :cljs (:require [cljsjs.moment]))
  #?(:clj (:import (java.util Date))))

(defn release-to-latest
  "On Clojure side we use DateTime, whilst on ClojureScript side we use Moment."
  [release]
  #?(:clj  (condp #(when (%1 %2) %2) release
             string? :>> #(f/parse (f/formatter "yyyy-MM") %)
             int? :>> t/date-time
             coll? :>> #(t/latest (map release-to-latest (mapcat vals %)))
             #(instance? Date %) :>> c/to-date-time)
     :cljs (condp #(when (%1 %2) %2) release
             string? :>> #(.utcOffset (js/moment % "YYYY-MM") 0 true)
             int? :>> #(.utcOffset (js/moment (str %) "YYYY") 0 true)
             inst? :>> #(.utcOffset (js/moment %) 0 false)
             coll? :>> #(let [max-release (->> (mapcat vals %)
                                               (map release-to-latest)
                                               clj->js
                                               (.max js/moment))]
                          (.utcOffset max-release false))
             nil)))

(defn sort-latest-device
  [phones]
  (reverse (sort-by (juxt (comp #(.valueOf %) :lineage-wiki/latest-release)
                          (comp min :lineage-wiki/price-tags)
                          ;; also sort by name, so X Pro Plus 2 is after X Pro,
                          ;; even when release dates are the same.
                          :lineage-wiki/name)
                    phones)))

(def api-directory-content-prefix
  "https://api.github.com/repos/LineageOS/lineage_wiki/contents/")

(defn api-directory-content-url
  [path]
  (str api-directory-content-prefix path))

(def raw-master-blob-prefix
  "https://raw.githubusercontent.com/LineageOS/lineage_wiki/master/")

(defn phone-image-url
  [phone]
  (str raw-master-blob-prefix "images/devices/" (:lineage-wiki/image phone)))

(defn phone-spec-file-url
  [file]
  (str raw-master-blob-prefix (:path file)))

(defn version->human
  [version]
  (get {13.0 "Marshmallow"
        14.1 "Nougat"
        15.1 "Oreo"}
       version
       "unknown version"))

(defn version-logo
  [version]
  (get {13.0 "https://upload.wikimedia.org/wikipedia/en/0/0d/Android_Marshmallow_logo.png"
        14.1 "https://upload.wikimedia.org/wikipedia/commons/6/65/Android_Nougat_logo.png"
        15.1 "https://upload.wikimedia.org/wikipedia/commons/2/26/Android_Oreo_8.1_logo.svg"}
       version))

(defn phone+derived-values
  [phone]
  (assoc phone
    :lineage-wiki/display-card-status :collapsed
    :lineage-wiki/highest-version (->> (:lineage-wiki/versions phone)
                          (remove nil?)
                          (apply max))
    :lineage-wiki/latest-release (release-to-latest (:lineage-wiki/release phone))))

;; Should it be completed?
(defn phone-id
  "Allow reconciliation across multiple providers. Also used as React component key."
  [phone]
  (let [latest-release (read-string (:lineage-wiki/latest-release phone) "#?(:clj  c/to-long\n                              :cljs (.valueOf))")]
    ;; This should really allow only one unique phone per id or bad thing could happen.
    [(:lineage-wiki/vendor phone) (:lineage-wiki/name phone) latest-release]))

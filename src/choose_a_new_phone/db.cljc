(ns choose-a-new-phone.db
  (:require [cognitect.transit :as t]
            [choose-a-new-phone.domain :as domain])
  #?(:cljs (:require-macros [choose-a-new-phone.core :refer [dehydrated-default-db]])))

(def ui-default-page-lenth
  "Educated guess. Should be enough to make `choose-a-new-phone.components.visible-back-stop/check-visible-fn` work well."
  12)

(defn default-db
  "Show intent. Only give keys as dehydrated db will replace values"
  []
  #?(:clj  {:id->phone {}
            :pending-phone-request 0
            :ui/phone-list-length ui-default-page-lenth}
     :cljs (let [dehydrated-db (dehydrated-default-db)]
             (t/read (t/reader :json {:handlers {"datetime" (fn [s] (js/moment s))}}) dehydrated-db))))

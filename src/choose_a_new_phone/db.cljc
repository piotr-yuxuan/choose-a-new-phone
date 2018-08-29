(ns choose-a-new-phone.db
  (:require [cognitect.transit :as t]
            [choose-a-new-phone.domain :as domain])
  #?(:cljs (:require-macros [choose-a-new-phone.core :refer [dehydrated-default-db]])))

(defn default-db
  "Show intent. Only give keys as dehydrated db will replace values"
  []
  #?(:clj  {:id->phone {}
            :pending-phone-request 0}
     :cljs (let [dehydrated-db (dehydrated-default-db)]
             (-> (t/reader :json {:handlers {"datetime" (fn [s] (js/moment s))}})
                 (t/read dehydrated-db)))))

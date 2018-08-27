(ns choose-a-new-phone.db
  (:require [cognitect.transit :as t]
            [choose-a-new-phone.domain :as domain])
  (:require-macros [choose-a-new-phone.core :refer [dehydrated-default-db]]))

(defn phone+derived-values
  ;; This should be renamed and moved elsewhere.
  [phone]
  (assoc phone
    :display-card-status :collapsed
    :highest-version (->> (:versions phone)
                          (remove nil?)
                          (apply max))
    :latest-release (domain/release-to-latest (:release phone))))

(defn default-db
  "Show intent. Only give keys as dehydrated db will replace values"
  []
  (let [dehydrated-db (dehydrated-default-db)]
    (-> (t/reader :json)
        (t/read dehydrated-db)
        (update :phones #(map phone+derived-values %)))))

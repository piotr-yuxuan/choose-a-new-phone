(ns choose-a-new-phone.db)

(def default-db
  {:phone #{}
   :only-expanded? false
   :available-resource? #{}
   :pending-phone-request 0})

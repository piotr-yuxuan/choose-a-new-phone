(ns choose-a-new-phone.subs
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.domain :as domain]))

(re-frame/reg-sub
  ::phones
  (fn [db _]
    (->> db
         :id->phone
         vals)))

(re-frame/reg-sub
  ::sorted-phones
  :<- [::phones]
  (fn [phones _]
    (->> phones
         domain/sort-latest-device)))

(re-frame/reg-sub
  ::phone-dialog
  (fn [db _]
    (:phone-dialog db)))

(re-frame/reg-sub
  ::pending-phone-request?
  (fn [db _]
    (not (zero? (:pending-phone-request db)))))

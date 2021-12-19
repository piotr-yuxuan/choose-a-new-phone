(ns choose-a-new-phone.subs
  (:require [choose-a-new-phone.domain :as domain]
            [re-frame.core :as re-frame]))

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
    (domain/sort-latest-device phones)))

(re-frame/reg-sub
  ::phone-dialog
  (fn [db _]
    (:phone-dialog db)))

(re-frame/reg-sub
  ::pending-phone-request?
  (fn [db _]
    (not (zero? (:pending-phone-request db)))))

(re-frame/reg-sub
  ::phone-list-length
  (fn [db _]
    (:ui/phone-list-length db)))

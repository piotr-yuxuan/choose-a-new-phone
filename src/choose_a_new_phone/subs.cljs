(ns choose-a-new-phone.subs
  (:require [re-frame.core :as re-frame]
            [goog.object :as object]
            [choose-a-new-phone.events :as events]
            [choose-a-new-phone.domain :as domain]))

(re-frame/reg-sub-raw
  ::phones
  (fn [app-db _]
    (let [need-refresh? empty?]
      (when (need-refresh? (:phone @app-db))
        (re-frame/dispatch [::events/ls-dir]))
      (reagent.ratom/make-reaction
        (fn [] (:phone @app-db))
        :on-dispose (fn [])))))

(re-frame/reg-sub
  ::phone-card-loaded?
  (fn [db [_ phone]]
    (->> db
         :phone
         (some #{phone})
         :display-card-status)))

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

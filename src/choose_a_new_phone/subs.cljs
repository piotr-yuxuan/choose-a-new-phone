(ns choose-a-new-phone.subs
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.events :as events]
            [choose-a-new-phone.domain :as domain]))

(re-frame/reg-sub
  ::phone-card-loaded?
  (fn [db [_ phone]]
    (:display-card-status (some #{phone} (:phone db)))))

(re-frame/reg-sub
  ::display-phones?
  (fn [db _]
    (boolean (->> db
                  :phone
                  (map :display-card-status)
                  seq
                  (some some?)))))

(re-frame/reg-sub-raw
  ::phones
  (fn [app-db _]
    (let [need-refresh? empty?
          get-phones-data :phone]
      (when (need-refresh? (get-phones-data @app-db))
        (re-frame/dispatch [::events/ls-dir]))
      (reagent.ratom/make-reaction
        (fn [] (get-phones-data @app-db))
        :on-dispose (fn [])))))

(re-frame/reg-sub
  ::sorted-phones
  :<- [::phones]
  (fn [phones _]
    (domain/sort-latest-device phones)))

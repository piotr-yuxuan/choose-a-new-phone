(ns choose-a-new-phone.events.ui
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.db :as db])
  (:require-macros [choose-a-new-phone.core :as clj-core]))

(re-frame/reg-event-db
  ::phone-dialog
  (fn [db [_ {:keys [phone open?]}]]
    (assoc db
      :phone-dialog {:phone phone
                     :open? open?})))

(re-frame/reg-event-db
  ::initialize-db
  (fn [db _]
    (merge db/default-db
           (clj-core/dehydrated-db))))

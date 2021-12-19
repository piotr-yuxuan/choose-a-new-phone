(ns choose-a-new-phone.events.ui
  (:require [choose-a-new-phone.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
  ::phone-dialog
  (fn [db [_ {:keys [phone open?]}]]
    (assoc db
      :phone-dialog {:phone phone
                     :open? open?})))

(re-frame/reg-event-db
  ::initialize-db
  (fn [db _]
    (db/default-db)))

(re-frame/reg-event-db
  ::display-more-phones
  (fn [db _]
    (update db :ui/phone-list-length + db/ui-default-page-lenth)))

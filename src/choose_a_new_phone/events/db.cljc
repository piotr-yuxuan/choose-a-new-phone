(ns choose-a-new-phone.events.db
  (:require [choose-a-new-phone.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [db _]
    (db/default-db)))

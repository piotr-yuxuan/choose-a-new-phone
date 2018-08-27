(ns choose-a-new-phone.events.db
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.db :as db]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [db _]
    (db/default-db)))

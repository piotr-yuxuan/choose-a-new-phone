(ns choose-a-new-phone.events.bestbuy
  (:require [ajax.core :as ajax]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-fx
  ::fetch-phone
  (fn [_ [_ file]]
    {:http-xhrio {:method :get
                  :uri (domain/phone-spec-file-url file)
                  :timeout 8000 ;; optional see API docs
                  :response-format (ajax/raw-response-format)
                  :on-success [::fetch-phone-success file]
                  :on-failure [::fetch-phone-failure]}}))

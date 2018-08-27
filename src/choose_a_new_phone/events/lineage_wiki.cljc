(ns choose-a-new-phone.events.lineage-wiki
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.domain :as domain]
            [ajax.core :as ajax]
            [choose-a-new-phone.effects :as effects]
            [choose-a-new-phone.utils :as utils]))

(re-frame/reg-event-fx
  ::get-phone-list
  (fn [_ _]
    {::effects/http-xhrio {:method :get
                           :uri (domain/api-directory-content-url "_data/devices")
                           :timeout 8000 ;; optional see API docs
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success [::get-phone-list-success]
                           :on-failure [::get-phone-list-failure]}}))

(re-frame/reg-event-fx
  ::get-phone-list-success
  (fn [{:keys [db]} [_ result]]
    {:db (assoc db :pending-phone-request (count result))
     ::effects/http-xhrio (->> result
                               shuffle ;; break lexicographic order
                               (map (fn fetch-phone [file]
                                      {:method :get
                                       :uri (domain/phone-spec-file-url file)
                                       :timeout 8000 ;; optional see API docs
                                       :response-format (ajax/raw-response-format)
                                       :on-success [::fetch-phone-success file]
                                       :on-failure [::fetch-phone-failure file]})))}))

(re-frame/reg-event-fx
  ::get-phone-list-failure
  (fn [_ _]
    {}))

(re-frame/reg-event-fx
  ::fetch-phone-failure
  (fn [{:keys [db]} [_ file]]
    {:db (update db :pending-phone-request dec)}))

(defn enrich-phone-result
  [file result]
  (when-let [phone (utils/yaml->map result)]
    (assoc phone
      :file file)))

(re-frame/reg-event-fx
  ::fetch-phone-success
  (fn [{:keys [db]} [_ file result]]
    {:db (update db :pending-phone-request dec)
     :dispatch [::parse-yaml-file file result]}))

(re-frame/reg-event-db
  ::parse-yaml-file
  (fn [db [_ file result]]
    (if-let [phone (some->> result
                            (enrich-phone-result file)
                            domain/phone+derived-values)]
      (assoc-in db [:id->provider->phone :lineage-wiki (domain/phone-id phone)] phone)
      (update db :failed-parsing conj #:file{:path file
                                             :content result}))))


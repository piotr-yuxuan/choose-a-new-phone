(ns choose-a-new-phone.events
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.db :as db]
            [ajax.core :as ajax]
            [choose-a-new-phone.utils :as utils]
            [choose-a-new-phone.domain :as domain]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [db _]
    db/default-db))

(re-frame/reg-event-fx
  ::phone-card-status
  (fn [{:keys [db]} [_ phone status]]
    {:db (-> db
             (assoc :only-expanded? false) ;; avoid collapse the last expanded
             (update :phone
                     (comp #(conj % (assoc phone
                                      :display-card-status status))
                           #(disj % phone))))}))

(re-frame/reg-event-fx
  ::try-raw-good-http-result
  (fn [{:keys [db]} [_ file result]]
    (let [phone (utils/yaml->map result)
          latest-release (domain/release-to-latest (:release phone))]
      (if latest-release
        {:db (update db
                     :phone
                     (fn [phones]
                       (set (conj phones
                                  (assoc phone
                                    :file file
                                    :display-card-status :collapsed
                                    :highest-version (->> (:versions phone)
                                                          (remove nil?)
                                                          (apply max))
                                    :latest-release latest-release)))))}
        (do
          (println "failing release" (:path file))
          {})))))

(re-frame/reg-event-fx
  ::try-raw-bad-http-result
  (fn [_ _]
    {}))

(re-frame/reg-event-fx
  ::bad-http-result
  (fn [_ _]
    {}))

(re-frame/reg-event-fx
  ::ls-dir
  (fn [_ _]
    {:http-xhrio {:method :get
                  :uri (domain/api-directory-content-url "_data/devices")
                  :timeout 8000 ;; optional see API docs
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::ls-dir-http-result]
                  :on-failure [::bad-http-result]}}))

(re-frame/reg-fx
  :throttle-dispatch-n
  (fn dumb-throttling [[delay eventv-list]]
    ;; TODO use core.async in cljs if available
    (doseq [[i eventv] (map-indexed vector eventv-list)]
      (js/setTimeout #(re-frame/dispatch eventv)
                     (* (inc i)
                        delay)))))

(re-frame/reg-event-fx
  ::ls-dir-http-result
  (fn [_ [_ result]]
    {:throttle-dispatch-n [0 (->> result
                                  shuffle ;; break lexicographic order
                                  (map #(do [::cat-file %])))]}))

(re-frame/reg-event-fx
  ::cat-file
  (fn [_ [_ file]]
    {:http-xhrio {:method :get
                  :uri (domain/phone-spec-file-url file)
                  :timeout 8000 ;; optional see API docs
                  :response-format (ajax/raw-response-format)
                  :on-success [::try-raw-good-http-result file]
                  :on-failure [::try-raw-bad-http-result]}}))

(re-frame/reg-event-db
  ::only-expanded?
  (fn [db [_ value]]
    (assoc db :only-expanded? value)))

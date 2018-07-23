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

(defn conj-non-nil-phone
  [phones phone file latest-release]
  (if (nil? phone)
    phones
    (conj phones
          (assoc phone
            :file file
            :display-card-status :collapsed
            :highest-version (->> (:versions phone)
                                  (remove nil?)
                                  (apply max))
            :latest-release latest-release))))

(re-frame/reg-event-fx
  ::try-raw-good-http-result
  (fn [{:keys [db]} [_ file result]]
    (let [phone (utils/yaml->map result)
          latest-release (domain/release-to-latest (:release phone))]
      (when (nil? phone)
        (println "failing yaml parsing" (:path file)))
      {:db (-> db
               (update :pending-phone-request dec)
               (update :phone conj-non-nil-phone phone file latest-release))})))

(re-frame/reg-event-db
  ::try-raw-bad-http-result
  (fn [db _]
    (update db :pending-phone-request dec)))

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
  (fn [{:keys [db]} [_ result]]
    {:db (assoc db :pending-phone-request (count result))
     :throttle-dispatch-n [10 (->> result
                                   shuffle ;; break lexicographic order
                                   (map (fn [file-path] [::cat-file file-path])))]}))

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
  ::phone-dialog
  (fn [db [_ {:keys [phone open?]}]]
    (assoc db
      :phone-dialog {:phone phone
                     :open? open?})))

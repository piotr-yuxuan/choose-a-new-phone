(ns choose-a-new-phone.events.lineage-wiki
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.domain :as domain]
            [ajax.core :as ajax]
            [choose-a-new-phone.effects :as effects]
            [choose-a-new-phone.utils :as utils]))

(re-frame/reg-event-fx
  ::get-phone-list
  (fn [_ _]
    {:http-xhrio {:method :get
                  :uri (domain/api-directory-content-url "_data/devices")
                  :timeout 8000 ;; optional see API docs
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::get-phone-list-success]
                  :on-failure [::get-phone-list-failure]}}))

(re-frame/reg-event-fx
  ::get-phone-list-success
  (fn [{:keys [db]} [_ result]]
    {:db (assoc db :pending-phone-request (count result))
     ::effects/throttle-dispatch-n [10 (->> result
                                            shuffle ;; break lexicographic order
                                            (map (fn [file-path] [::fetch-phone file-path])))]}))

(re-frame/reg-event-fx
  ::get-phone-list-failure
  (fn [_ _]
    {}))

(re-frame/reg-event-fx
  ::fetch-phone
  (fn [_ [_ file]]
    {:http-xhrio {:method :get
                  :uri (domain/phone-spec-file-url file)
                  :timeout 8000 ;; optional see API docs
                  :response-format (ajax/raw-response-format)
                  :on-success [::fetch-phone-success file]
                  :on-failure [::fetch-phone-failure file]}}))

(re-frame/reg-event-fx
  ::fetch-phone-failure
  (fn [{:keys [db]} [_ file]]
    {::effects/println (str "failed to fetch" (:path file))
     :db (update db :pending-phone-request dec)}))

(defn lineage-wiki->phone
  [file result]
  (when-let [phone (utils/yaml->map result)]
    (assoc phone
      :file file
      :display-card-status :collapsed
      :highest-version (->> (:versions phone)
                            (remove nil?)
                            (apply max))
      :latest-release (domain/release-to-latest (:release phone)))))

(defn- temp-hydrate-phone
  [temp-dehydrated phone]
  (when phone
    (assoc phone
      :price-hint (some #(and (= (:name phone) (:name %))
                              (= (.format (:latest-release phone) "YYYY")
                                 (:release-year %))
                              (:price-hint %))
                        temp-dehydrated))))

(re-frame/reg-event-fx
  ::fetch-phone-success
  (fn [{:keys [db]} [_ file result]]
    (if-let [phone (->> result
                        (lineage-wiki->phone file)
                        (temp-hydrate-phone (:temp-dehydrated db)))]
      {:db (-> db
               (update :pending-phone-request dec) ;; interceptor?
               (update :phones conj phone))}
      {::effects/println (str "failing yaml parsing" (:path file))
       :db (update db :pending-phone-request dec)})))

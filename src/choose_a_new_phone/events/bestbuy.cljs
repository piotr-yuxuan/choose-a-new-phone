(ns choose-a-new-phone.events.bestbuy
  (:require [ajax.core :as ajax]
            [choose-a-new-phone.effects :as effects]
            [re-frame.core :as re-frame]
            [clojure.string :as str]))

(re-frame/reg-event-fx
  ::fetch-phone-price
  (fn [_ [_ phone]]
    (let [manufacturer (str/lower-case (:vendor phone))
          ;; plausible-name should be stripped of name suffixes.
          plausible-name (js/encodeURIComponent (str/lower-case (:name phone)))]
      {::effects/println "fetching phone price"
       :http-xhrio {:method :get
                    :uri (str "https://api.bestbuy.com/v1/products(releaseDate>=2018-01-01&releaseDate<=2018-12-31&manufacturer=" manufacturer "&name=" plausible-name "*)")
                    :params {"show" "name,salePrice"
                             "format" "json"
                             "apiKey" ""}
                    :timeout 8000 ;; optional see API docs
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success [::fetch-phone-success phone]
                    :on-failure [::fetch-phone-failure] ;;::fetch-phone-failure doesn't exit -> fix it
                    }})))

(re-frame/reg-event-fx
  ::fetch-phone-success
  (fn [{:keys [db]} [_ phone result]]
    (println "result" result)
    (let [maybe-accurate-result (->> result
                                     :products
                                     first)]
      (if (str/includes? (or (:name maybe-accurate-result) "") (:name phone))
        (let [price-hint-dollars (:salePrice maybe-accurate-result)]
          (println "price found!" price-hint-dollars)
          {:db (-> db
                   (update :phones disj phone) ;; cljs.core/replace ?
                   (update :phones conj (assoc phone :price-hint-dollars price-hint-dollars)))})
        {}))))
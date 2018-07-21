(ns choose-a-new-phone.subs
  (:require [re-frame.core :as re-frame]
            [goog.object :as object]
            [choose-a-new-phone.events :as events]
            [choose-a-new-phone.domain :as domain]))

(re-frame/reg-sub
  ::phone-card-loaded?
  (fn [db [_ phone]]
    (:display-card-status (some #{phone} (:phone db)))))

(re-frame/reg-sub-raw
  ::phones
  (fn [app-db _]
    (let [need-refresh? empty?
          get-phones-data :phone]
      (when (need-refresh? (get-phones-data @app-db))
        (re-frame/dispatch [::events/ls-dir]))
      (reagent.ratom/make-reaction
        (fn [] (get-phones-data @app-db))
        :on-dispose (fn [])))))

(re-frame/reg-sub
  ::only-expanded?
  (fn [db _]
    (:only-expanded? db)))

(re-frame/reg-sub
  ::sorted-phones
  :<- [::phones]
  :<- [::only-expanded?]
  (fn [[phones only-expanded?] _]
    (let [filter-phone (if only-expanded?
                         (comp #(= % :expanded) :display-card-status)
                         #(do true))]
      (->> phones
           domain/sort-latest-device
           (filter filter-phone)))))

(re-frame/reg-sub
  ::some-expanded-phones?
  :<- [::phones]
  (fn [phones _]
    (some #(->> %
                :display-card-status
                (= :expanded))
          phones)))

(re-frame/reg-sub-raw
  ;; Take care of CORS
  ::available-resource?
  (fn [app-db [_ src]]
    (assoc-in @app-db [:available-resource? src] false)
    (doto (.createElement js/document "img") ;; TODO hackish?
      ;; actually triggers a GET, but we can't it use right here
      (object/set "src" src)
      (.addEventListener "load" #(swap! app-db assoc-in [:available-resource? src] true)))
    (reagent.ratom/make-reaction
      #(when (get-in @app-db [:available-resource? src])
         src)
      :on-dispose #(swap! app-db update :available-resource? dissoc src))))

(re-frame/reg-sub
  ::cat-files-finished?
  (fn [db _]
    (:cat-files-finished? db)))

(ns choose-a-new-phone.subs
  (:require [re-frame.core :as re-frame]
            [goog.object :as object]
            [choose-a-new-phone.events :as events]
            [choose-a-new-phone.domain :as domain]))

(re-frame/reg-sub-raw
  ::phones
  (fn [app-db _]
    (let [need-refresh? empty?]
      (when (need-refresh? (:phone @app-db))
        (re-frame/dispatch [::events/ls-dir]))
      (reagent.ratom/make-reaction
        (fn [] (get-phones-data @app-db))
        :on-dispose (fn [])))))

(defn- maybe-get!
  [app-db img-src]
  (assoc-in @app-db [:available-resource? img-src] false)
  (doto (.createElement js/document "img")
    (object/set "src" img-src) ;; triggers a GET
    (.addEventListener "load" #(swap! app-db assoc-in [:available-resource? img-src] true))))

(re-frame/reg-sub-raw
  ::available-resource?
  (fn [app-db [_ src]]
    (maybe-get! app-db src)
    (reagent.ratom/make-reaction
      #(when (get-in @app-db [:available-resource? src])
         src)
      :on-dispose #(swap! app-db update :available-resource? dissoc src))))

(re-frame/reg-sub
  ::phone-card-loaded?
  (fn [db [_ phone]]
    (->> db
         :phone
         (some #{phone})
         :display-card-status)))

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

(re-frame/reg-sub
  ::cat-files-finished?
  (fn [db _]
    (:cat-files-finished? db)))

(ns choose-a-new-phone.components.main
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.subs :as subs]
            [cljs-react-material-ui.reagent :as mui]
            [choose-a-new-phone.components.app-bar :refer [app-bar]]
            [choose-a-new-phone.components.phone-dialog :refer [phone-dialog]]
            [choose-a-new-phone.components.phone-card :refer [phone-card]]))

(defn panel
  []
  (let [sorted-phones (seq @(re-frame/subscribe [::subs/sorted-phones]))
        display-phones? (and (< 190 (count sorted-phones))
                             (seq sorted-phones))]
    [:div (when-not display-phones?
            {:style {:display :flex
                     :height "100%"
                     :flex-direction :column}})
     [app-bar]
     (when-let [dialog-state @(re-frame/subscribe [::subs/phone-dialog])]
       [phone-dialog dialog-state])
     [:div {:style {:display :flex
                    :flex-direction :row
                    :flex-wrap :wrap
                    :justify-content :space-evenly
                    :align-items :center
                    :align-content :stretch
                    :padding 5}}
      (->> sorted-phones
           (map phone-card)
           doall)]]))

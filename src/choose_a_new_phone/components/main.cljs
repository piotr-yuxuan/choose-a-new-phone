(ns choose-a-new-phone.components.main
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.subs :as subs]
            [choose-a-new-phone.events :as events]
            [cljs-react-material-ui.reagent :as mui]
            [choose-a-new-phone.components.app-bar :refer [app-bar]]
            [choose-a-new-phone.components.phone-dialog :refer [phone-dialog]]
            [choose-a-new-phone.components.phone-card :refer [phone-card]]))

(defn panel
  []
  (let [sorted-phones (seq @(re-frame/subscribe [::subs/sorted-phones]))]
    [:div (when-not sorted-phones
            {:style {:display :flex
                     :height "100%"
                     :flex-direction :column}})
     [app-bar]
     (when-let [dialog-state @(re-frame/subscribe [::subs/phone-dialog])]
       [phone-dialog dialog-state])
     (if sorted-phones
       [:div {:style {:display :flex
                      :flex-direction :row
                      :flex-wrap :wrap
                      :justify-content :space-evenly
                      :align-items :center
                      :align-content :stretch
                      :padding 5}}
        (->> sorted-phones
             (map phone-card)
             doall)]
       [:div {:style {:flex 1
                      :display :flex
                      :align-items :center
                      :justify-content :center}}
        [mui/circular-progress {:size 70}]])]))

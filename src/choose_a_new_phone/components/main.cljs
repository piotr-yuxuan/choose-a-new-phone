(ns choose-a-new-phone.components.main
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.subs :as subs]
            [cljs-react-material-ui.reagent :as mui]
            [cljs-react-material-ui.icons :as ic]
            [choose-a-new-phone.components.app-bar :refer [app-bar]]
            [choose-a-new-phone.components.phone-card :refer [phone-card]]))

(defn panel
  []
  (let [sorted-phones (seq @(re-frame/subscribe [::subs/sorted-phones]))
        display-phones? @(re-frame/subscribe [::subs/display-phones?])]
    [:div {:style (when-not display-phones?
                    {:display :flex
                     :height "100%"
                     :flex-direction :column})}
     [app-bar]
     (if sorted-phones
       [:div {:style {:display :flex
                      :flex-direction :row
                      :flex-wrap :wrap
                      :justify-content :space-evenly
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

(ns choose-a-new-phone.components.main
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.subs :as subs]
            [choose-a-new-phone.events :as events]
            [cljs-react-material-ui.reagent :as mui]
            [cljs-react-material-ui.icons :as ic]
            [choose-a-new-phone.components.app-bar :refer [app-bar]]
            [choose-a-new-phone.components.phone-card :refer [phone-card]]))

(defn panel
  []
  (let [sorted-phones (seq @(re-frame/subscribe [::subs/sorted-phones]))]
    [:div (when-not sorted-phones
            {:style {:display :flex
                     :height "100%"
                     :flex-direction :column}})
     [app-bar]
     [:div {:style {:margin 20
                    :width "50%"}}
      [mui/toggle {:label "Only expanded devices"
                   :label-position :right
                   :on-toggle #(re-frame/dispatch [::events/only-expanded? %2])
                   :toggled @(re-frame/subscribe [::subs/only-expanded?])
                   :disabled (not @(re-frame/subscribe [::subs/some-expanded-phones?]))}]]
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

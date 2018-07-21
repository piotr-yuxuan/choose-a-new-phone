(ns choose-a-new-phone.components.app-bar
  (:require [re-frame.core :as re-frame]
            [cljs-react-material-ui.reagent :as mui]
            [choose-a-new-phone.subs :as subs]))

(defn lineage-os-logo
  []
  [:div {:style {:display :inline-flex
                 :align-items :center
                 :justify-content :center}}
   [:img {:src "https://upload.wikimedia.org/wikipedia/commons/b/be/Lineage_OS_Logo.png"
          :style {:object-fit :contain
                  :height 50
                  :width 50}}]])

(defn phone-retrieval-progress
  []
  [:div {:style {:display :inline-flex
                 :align-items :center
                 :flex-direction :row
                 :justify-content :center}}
   [:div {:style {:margin-right 15}} (count @(re-frame/subscribe [::subs/phones]))]
   [mui/circular-progress {:color :white}]])

(defn app-bar
  []
  [mui/app-bar {:title "Choose you next LineageOS phone"
                :iconElementLeft (reagent.core/as-component [lineage-os-logo])
                :iconElementRight (reagent.core/as-component
                                    (when-not @(re-frame/subscribe [::subs/cat-files-finished?])
                                      [phone-retrieval-progress]))}])

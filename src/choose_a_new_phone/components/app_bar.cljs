(ns choose-a-new-phone.components.app-bar
  (:require [cljs-react-material-ui.reagent :as mui]))

(defn lineage-os-logo
  []
  [:div {:style {:display :inline-flex
                 :align-items :center
                 :justify-content :center}}
   [:img {:src "https://upload.wikimedia.org/wikipedia/commons/b/be/Lineage_OS_Logo.png"
          :style {:object-fit :contain
                  :height 50
                  :width 50}}]])

(defn app-bar
  []
  [mui/app-bar {:title "Choose you next LineageOS phone"
                :iconElementLeft (reagent.core/as-component [lineage-os-logo])}])

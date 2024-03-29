(ns choose-a-new-phone.components.phone-card
  (:require [choose-a-new-phone.components.progressive-img :refer [progressive-img]]
            [choose-a-new-phone.domain :as domain]
            [choose-a-new-phone.events.ui :as ui]
            [choose-a-new-phone.subs :as subs]
            [cljs-react-material-ui.reagent :as mui]
            [clojure.string :as str]
            [re-frame.core :as re-frame]))

(defn phone-card
  [phone]
  [:div {:style {:width "33%"
                 :min-width 380}
         :key (domain/phone-id phone)}
   [mui/card {:container-style {:width "100%"}
              :className "hover-zoom-5"
              :style {:margin 15
                      :transition "all .2s ease-in-out"
                      :display :flex}
              :on-click #(re-frame/dispatch [::ui/phone-dialog {:phone phone
                                                                :open? true}])
              :expanded (= phone (:phone @(re-frame/subscribe [::subs/phone-dialog])))}
    [mui/card-header {:title (str/join " " [(:lineage-wiki/vendor phone) (:lineage-wiki/name phone)])
                      :subtitle (str "Device released " (.fromNow (:lineage-wiki/latest-release phone)))
                      :avatar (domain/version-logo (:lineage-wiki/highest-version phone))
                      :showExpandableButton true}]
    [mui/card-text {:style {:display :flex
                            :justify-content :space-around
                            :flex-flow "row wrap"}}
     [progressive-img {:style {:height 300
                               :width 300
                               :object-fit :contain}
                       :src (domain/phone-image-url phone)}]]]])

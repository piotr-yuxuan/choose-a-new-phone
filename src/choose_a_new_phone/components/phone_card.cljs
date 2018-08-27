(ns choose-a-new-phone.components.phone-card
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.subs :as subs]
            [choose-a-new-phone.events.ui :as ui]
            [clojure.string :as str]
            [choose-a-new-phone.domain :as domain]
            [choose-a-new-phone.components.progressive-img :refer [progressive-img]]
            [cljs-react-material-ui.reagent :as mui]))

(defn phone-card
  [phone] ;; will get rendered each time object attribute :latest-release is modified; suboptimal?
  [:div {:style {:width "33%"
                 :min-width 380}
         :key (hash phone)}
   [mui/card {:container-style {:width "100%"}
              :className "hover-zoom-5"
              :style {:margin 15
                      :transition "all .2s ease-in-out"
                      :display :flex}
              :on-click #(re-frame/dispatch [::ui/phone-dialog {:phone phone
                                                                :open? true}])
              :expanded (= phone (:phone @(re-frame/subscribe [::subs/phone-dialog])))}
    [mui/card-header {:title (str/join " " [(:vendor phone) (:name phone)])
                      :subtitle (str "Device released " (.fromNow (:latest-release phone)))
                      :avatar (domain/version-logo (:highest-version phone))
                      :showExpandableButton true}]
    [mui/card-text {:style {:display :flex
                            :justify-content :space-around
                            :flex-flow "row wrap"}}
     [progressive-img {:style {:height 300
                               :width 300
                               :object-fit :contain}
                       :src (domain/phone-image-url phone)}]]]])

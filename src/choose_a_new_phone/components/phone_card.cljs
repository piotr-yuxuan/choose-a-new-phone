(ns choose-a-new-phone.components.phone-card
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.subs :as subs]
            [choose-a-new-phone.events :as events]
            [clojure.string :as str]
            [cljs-react-material-ui.icons :as ic]
            [choose-a-new-phone.domain :as domain]
            [choose-a-new-phone.components.progressive-img :refer [progressive-img]]
            [cljs-react-material-ui.reagent :as mui]))

(defn phone-card
  [phone]
  [:div {:style {:width "33%"
                 :min-width 380}
         :key (hash phone)}
   [mui/card {:container-style {:width "100%"}
              :style {:margin 15
                      :display (if @(re-frame/subscribe [::subs/phone-card-loaded? phone])
                                 :flex
                                 :none)}
              :expanded (= :expanded @(re-frame/subscribe [::subs/phone-card-loaded? phone]))}
    [mui/card-header {:title (str/join " " [(:vendor phone) (:name phone)])
                      :subtitle (str (.format (:latest-release phone) "MMMM YYYY"))
                      :avatar (domain/version-logo (:highest-version phone))
                      :showExpandableButton true
                      :on-click #(let [new-state (if (= :expanded @(re-frame/subscribe [::subs/phone-card-loaded? phone]))
                                                   :collapsed
                                                   :expanded)]
                                   (re-frame/dispatch [::events/phone-card-status phone new-state]))}]
    [mui/card-text {:style {:display :flex
                            :justify-content :space-around
                            :flex-flow "row wrap"}}
     [progressive-img {:style {:height 300
                               :width 300
                               :object-fit :contain}
                       :src (domain/phone-image-url phone)}]

     (when (= :expanded @(re-frame/subscribe [::subs/phone-card-loaded? phone]))
       [mui/card-text {:style {:flex 1
                               :display :flex
                               :flex-flow "column wrap"
                               :justify-content :space-around}
                       :expandable true}
        [mui/list
         [mui/list-item {:primary-text (str (:cpu_freq phone) ", " (:cpu_cores phone) " cores")
                         :left-icon (ic/hardware-memory)}]
         [mui/list-item {:primary-text (str (:screen phone) ", " (:screen_res phone))
                         :left-icon (ic/hardware-phone-android)}]
         [mui/list-item {:primary-text (str (:storage phone) ", RAM " (:ram phone))
                         :left-icon (ic/device-storage)}]
         [mui/list-item {:primary-text (str (:capacity (:battery phone)) "mAh, " (:tech (:battery phone)))
                         :left-icon (ic/device-battery-std)}]
         [mui/list-item {:primary-text (:info (first (:cameras phone)))
                         :left-icon (ic/image-photo-camera)}]]])]]])

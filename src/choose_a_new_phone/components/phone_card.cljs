(ns choose-a-new-phone.components.phone-card
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.subs :as subs]
            [choose-a-new-phone.events :as events]
            [clojure.string :as str]
            [cljs-react-material-ui.icons :as ic]
            [choose-a-new-phone.domain :as domain]
            [cljs-react-material-ui.reagent :as mui]))

(defn phone-card
  [phone]
  [mui/card {:key (hash phone)
             :on-click #(let [new-state (if (= :expanded @(re-frame/subscribe [::subs/phone-card-loaded? phone]))
                                          :collapsed
                                          :expanded)]
                          (re-frame/dispatch [::events/phone-card-status phone new-state]))
             :style (merge {:margin 15 ;; TODO better use CSS grid
                            :display (if @(re-frame/subscribe [::subs/phone-card-loaded? phone])
                                       :flex
                                       :none)})
             :expanded (= :expanded @(re-frame/subscribe [::subs/phone-card-loaded? phone]))}
   [mui/card-header {:title (str/join " " [(:vendor phone) (:name phone)])
                     :subtitle (str (.format (:latest-release phone) "MMMM YYYY"))
                     :avatar (domain/version-logo (:highest-version phone))
                     :showExpandableButton true}]
   [mui/card-text {:actAsExpander true
                   :style {:display :flex
                           :flex-flow "row wrap"}}
    [mui/card-media {:style {:padding 30}}
     [:img {:src (domain/phone-image-url phone)
            :alt (:image phone)
            :style {:max-height 300
                    :max-width 300
                    :object-fit :contain}}]]

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
                        :left-icon (ic/image-photo-camera)}]]])]])

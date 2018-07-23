(ns choose-a-new-phone.components.phone-dialog
  (:require [re-frame.core :as re-frame]
            [choose-a-new-phone.events :as events]
            [clojure.string :as str]
            [cljs-react-material-ui.icons :as ic]
            [choose-a-new-phone.domain :as domain]
            [choose-a-new-phone.components.progressive-img :refer [progressive-img]]
            [cljs-react-material-ui.reagent :as mui]))

(defn phone-dialog
  []
  (fn [{:keys [phone open?]}]
    [mui/dialog {:modal false
                 :open open?
                 :title (str/join " " [(:vendor phone) (:name phone) (domain/version->human (:highest-version phone))])
                 :autoScrollBodyContent true
                 :onRequestClose #(re-frame/dispatch [::events/phone-dialog {:phone nil :open? false}])}
     [:div {:style {:display :flex
                    :justify-content :space-around
                    :flex-flow "row-reverse wrap"}}
      [:div {:style {:flex 1
                     :display :flex
                     :flex-flow "column wrap"
                     :justify-content :space-around}}
       [mui/list
        [mui/list-item {:primary-text (when (:latest-release phone)
                                        (str (.format (:latest-release phone) "MMMM YYYY")))
                        :left-icon (ic/action-perm-contact-calendar)}]
        [mui/list-item {:primary-text (str (:cpu_freq phone) ", " (:cpu_cores phone) " cores")
                        :left-icon (ic/hardware-memory)}]
        [mui/list-item {:primary-text (str (:screen phone) ", " (:screen_res phone))
                        :left-icon (ic/hardware-phone-android)}]
        [mui/list-item {:primary-text (str (:storage phone) ", RAM " (:ram phone))
                        :left-icon (ic/device-storage)}]
        [mui/list-item {:primary-text (str (:capacity (:battery phone)) "mAh, " (:tech (:battery phone)))
                        :left-icon (ic/device-battery-std)}]
        [mui/list-item {:primary-text (:info (first (:cameras phone)))
                        :left-icon (ic/image-photo-camera)}]]]
      [progressive-img {:style {:margin-top 15
                                :margin-bottom 15
                                :height 500
                                :width 500
                                :object-fit :contain}
                        :src (domain/phone-image-url phone)}]]]))

(ns choose-a-new-phone.components.phone-dialog
  (:require [choose-a-new-phone.components.progressive-img :refer [progressive-img]]
            [choose-a-new-phone.domain :as domain]
            [choose-a-new-phone.events.bestbuy :as bestbuy]
            [choose-a-new-phone.events.ui :as ui]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as mui]
            [clojure.string :as str]
            [re-frame.core :as re-frame]))

(defn phone-dialog
  [{:keys [phone open?]}]
  [mui/dialog {:modal false
               :open open?
               :title (str/join " " [(:lineage-wiki/vendor phone) (:lineage-wiki/name phone) (domain/version->human (:lineage-wiki/highest-version phone))])
               :autoScrollBodyContent true
               :onRequestClose #(re-frame/dispatch [::ui/phone-dialog {:phone nil :open? false}])}
   [:div {:style {:display :flex
                  :justify-content :space-around
                  :flex-flow "row-reverse wrap"}}
    [:div {:style {:flex 1
                   :display :flex
                   :flex-flow "column wrap"
                   :justify-content :space-around}}
     [mui/list
      [mui/list-item {:primary-text (when (:lineage-wiki/latest-release phone)
                                      (str (.format (:lineage-wiki/latest-release phone) "MMMM YYYY")))
                      :left-icon (ic/action-perm-contact-calendar)}]
      [mui/list-item {:primary-text (str (:lineage-wiki/cpu_freq phone) ", " (:lineage-wiki/cpu_cores phone) " cores")
                      :left-icon (ic/hardware-memory)}]
      [mui/list-item {:primary-text (str (:lineage-wiki/screen phone) ", " (:lineage-wiki/screen_res phone))
                      :left-icon (ic/hardware-phone-android)}]
      [mui/list-item {:primary-text (str (:lineage-wiki/storage phone) ", RAM " (:lineage-wiki/ram phone))
                      :left-icon (ic/device-storage)}]
      [mui/list-item {:primary-text (str (:capacity (:lineage-wiki/battery phone)) "mAh, " (:tech (:lineage-wiki/battery phone)))
                      :left-icon (ic/device-battery-std)}]
      [mui/list-item {:primary-text (:info (first (:lineage-wiki/cameras phone)))
                      :left-icon (ic/image-photo-camera)}]
      [mui/list-item {:primary-text (if-let [price-hint (:lineage-wiki/price-hint phone)]
                                      (str "~" price-hint "€")
                                      "no hint")
                      :left-icon (ic/editor-attach-money)}]]]
    [progressive-img {:style {:margin-top 15
                              :margin-bottom 15
                              :height 500
                              :width 500
                              :object-fit :contain}
                      :src (domain/phone-image-url phone)}]]])

(ns choose-a-new-phone.components.app-bar
  (:require [choose-a-new-phone.subs :as subs]
            [cljs-react-material-ui.reagent :as mui]
            [re-frame.core :as re-frame]))

(defn github-logo []
  [:svg {:version 1.1
         :height 32
         :width 32
         :viewBox "0 0 16 16"}
   [:path {:file-rule "evenodd"
           :d "M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0 0 16 8c0-4.42-3.58-8-8-8z"}]])

(defn lineage-os-logo
  []
  [:div {:style {:display :inline-flex
                 :align-items :center
                 :justify-content :center}}
   [:img {:src "https://upload.wikimedia.org/wikipedia/commons/b/be/Lineage_OS_Logo.png"
          :style {:object-fit :contain
                  :height 50
                  :width 50}}]])

;; TODO before disappear, show how many devices + fetching errors (allow retry), if any
(defn phone-retrieval-progress
  []
  [:div {:style {:display :inline-flex
                 :align-items :center
                 :flex-direction :row
                 :justify-content :center}}
   [:div {:style {:margin-right 15
                  :color :white}}
    (count @(re-frame/subscribe [::subs/phones]))]
   [mui/circular-progress {:color :white}]])

(defn repo-link
  []
  [:a {:href "https://github.com/piotr-yuxuan/choose-a-new-phone"
       :target "_blank"
       :style {:text-decoration :none
               :display :inline-flex
               :align-items :center}}
   [:span {:style {:align-self :baseline
                  :margin-right 5
                  :color :white
                  :font-style :oblique}} "source"]
   [github-logo]])

(def pray-emoticon "\uD83D\uDE4F")

(defn donation-link
  []
  [:a {:href "https://donorbox.org/help-us-help-you-choose-your-next-phone"
       :target "_blank"
       :style {:text-decoration :none
               :display :inline-flex
               :align-items :center}}
   [:span {:style {:align-self :baseline
                  :margin-right 5
                  :color :white
                  :font-style :oblique}} "donation"] [:span {:style {:font-size "1.2em"}} pray-emoticon]])

(defn title
  []
  [:div "Choose your next LineageOS phone (" [repo-link] ", " [donation-link] ")"])

(defn app-bar
  []
  [mui/app-bar {:title (reagent.core/as-component [title])
                :iconElementLeft (reagent.core/as-component [lineage-os-logo])
                :iconElementRight (reagent.core/as-component
                                    (when @(re-frame/subscribe [::subs/pending-phone-request?])
                                      [phone-retrieval-progress]))}])

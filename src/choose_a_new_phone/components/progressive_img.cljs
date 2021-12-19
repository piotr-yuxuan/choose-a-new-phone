(ns choose-a-new-phone.components.progressive-img
  (:require [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as mui]))

(defn progressive-img
  [el-attrs & progressive-img-attrs]
  (let [;; max 1 so this works even with one single image / el-attrs
        initial-layer-index (max 1 (count progressive-img-attrs))
        layer-index (reagent.core/atom initial-layer-index)
        error? (reagent.core/atom false)]
    (fn []
      (let [last-layer? (zero? (deref layer-index))
            first-layer? (= @layer-index initial-layer-index)]
        [:div (update el-attrs :style merge {:display :inline-flex
                                             :position :relative
                                             :vertical-align :bottom
                                             :align-items :center
                                             :justify-content :center})
         (when-not last-layer?
           ^{:key @layer-index}
           [:img (-> (merge-with merge el-attrs (nth progressive-img-attrs (dec @layer-index)))
                     (update :style merge {:display :inline
                                           :visibility :hidden})
                     (assoc :on-load #(swap! layer-index dec)
                            :on-error #(reset! error? true)))])
         (when-not first-layer?
           [:img (update (merge-with merge el-attrs (nth progressive-img-attrs (deref layer-index))) :style merge {:display :inline})])
         (when @error?
           [ic/alert-error-outline {:style {:position :relative
                                            :width "50%"
                                            :height "50%"}
                                    :on-click (fn retry []
                                                (reset! error? false)
                                                (swap! layer-index inc))}])
         (when-not (or @error? last-layer?)
           [mui/circular-progress {:style {:position :absolute}}])]))))

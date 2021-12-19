(ns choose-a-new-phone.components.visible-back-stop
  "Keep ip simple, stupid. This is enough for a simple lazy list."
  (:require [goog.object :as object]
            [reagent.core :as reagent]))

(defn check-visible-fn
  "Use thresholds. This implies that `on-visible-threshold` must result
  in filling the viewport and triggering `on-invisible-threshold` and
  it will be staled."
  [{:as opts
    :keys [on-visible-threshold on-invisible-threshold vertical-offset]
    :or {on-visible-threshold (fn [el] nil)
         on-invisible-threshold (fn [el] nil)
         vertical-offset 0}}
   previously-visible?
   el-id]
  (fn []
    (let [el (.getElementById js/document el-id)
          bounding-rect-top (object/get (.getBoundingClientRect el) "top")
          window-height (object/get js/window "innerHeight")
          visible? (and (< (- bounding-rect-top
                              vertical-offset)
                           window-height)
                        (<= (- (+ window-height
                                  vertical-offset))
                            bounding-rect-top))]
      (condp = [@previously-visible? visible?]
        [false true] (on-visible-threshold el)
        [true false] (on-invisible-threshold el)
        nil)
      (reset! previously-visible? visible?)
      nil)))

(def check-interval-default
  "in ms"
  200)

(defn visible-back-stop
  [{:as opts
    :keys [check-interval]
    :or {check-interval check-interval-default}}]
  (let [js-interval (atom nil)
        previously-visible? (atom false)
        el-id (name (gensym))]
    (reagent/create-class
      {:component-did-mount (fn []
                              (reset! js-interval
                                      (js/setInterval (check-visible-fn opts
                                                                        previously-visible?
                                                                        el-id)
                                                      check-interval)))
       :component-will-unmount (fn []
                                 (js/clearInterval @js-interval)
                                 (reset! js-interval nil))
       :reagent-render (fn [opts]
                         [:div {:id el-id
                                :style {:width 0
                                        :height 0
                                        :margin 0
                                        :padding 0}}])})))

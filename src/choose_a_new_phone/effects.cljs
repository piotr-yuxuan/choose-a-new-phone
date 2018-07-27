(ns choose-a-new-phone.effects
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-fx
  ::throttle-dispatch-n
  (fn dumb-throttling [[delay eventv-list]]
    ;; TODO use core.async in cljs if available
    (doseq [[i eventv] (map-indexed vector eventv-list)]
      (js/setTimeout #(re-frame/dispatch eventv)
                     (* (inc i)
                        delay)))))

(re-frame/reg-fx
  ::println
  (fn [& args]
    (apply println args)))

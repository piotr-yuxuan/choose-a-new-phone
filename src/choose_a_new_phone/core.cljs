(ns choose-a-new-phone.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            day8.re-frame.http-fx
            cljsjs.material-ui
            [choose-a-new-phone.events.ui :as ui]
            [choose-a-new-phone.components.main :as main]
            [choose-a-new-phone.config :as config]
            [cljs-react-material-ui.core :as cui]
            [cljs-react-material-ui.reagent :as mui]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root
  []
  (re-frame/clear-subscription-cache!)
  (reagent/render [mui/mui-theme-provider
                   {:mui-theme (cui/get-mui-theme)}
                   [main/panel]]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::ui/initialize-db])
  (dev-setup)
  (mount-root))

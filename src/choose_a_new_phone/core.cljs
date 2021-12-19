(ns choose-a-new-phone.core
  (:require [choose-a-new-phone.components.main :as main]
            [choose-a-new-phone.config :as config]
            [choose-a-new-phone.events.db :as events.db]
            [choose-a-new-phone.events.lineage-wiki :as lineage-wiki]
            [cljs-react-material-ui.core :as cui]
            [cljs-react-material-ui.reagent :as mui]
            cljsjs.material-ui
            day8.re-frame.http-fx
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

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
  (re-frame/dispatch-sync [::events.db/initialize-db])
  ;; temporary situation: we want to refresh the db once, even if we
  ;; already have dehydrated data.
  (re-frame/dispatch [::lineage-wiki/get-phone-list])
  (dev-setup)
  (mount-root))

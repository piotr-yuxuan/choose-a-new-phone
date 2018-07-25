(defproject choose-a-new-phone "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0-alpha6"]
                 [org.clojure/clojurescript "1.10.339"]
                 [reagent "0.8.2-SNAPSHOT"]
                 [cljs-react-material-ui "0.2.50"]
                 [cljsjs/react "16.4.1-0"] ;; I should exclude it from other project, right?
                 [cljsjs/react-dom "16.4.1-0"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [io.forward/yaml "1.0.9"] ;; yaml for Clojure
                 [cljsjs/js-yaml "3.3.1-0"] ;; yaml for ClojureScript
                 [clj-time "0.14.4"] ;; time for Clojure
                 [cljsjs/moment "2.22.2-0"] ;; time for ClojureScript
                 [re-frame "0.10.5"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-shell "0.5.0"]]
  :min-lein-version "2.5.3"
  :license {:name "GNU GPL v3+"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :source-paths ["src"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "resources/public/css/stylesheet.css"
                                    "docs"
                                    "target"]
  :figwheel {:css-dirs ["resources/public/css"]}
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [day8.re-frame/re-frame-10x "0.3.3-react16"] ;; keep "x.y.z-react16" suffix
                                  [cider/piggieback "0.3.6"]]
                   :plugins [[lein-figwheel "0.5.17-SNAPSHOT"]]}}
  :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel {:on-jsload "choose-a-new-phone.core/mount-root"}
                        :compiler {:main choose-a-new-phone.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true
                                   :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                                   :preloads [devtools.preload day8.re-frame-10x.preload]
                                   :external-config {:devtools/config {:features-to-install :all}}}}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:main choose-a-new-phone.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :closure-defines {goog.DEBUG false}
                                   :pretty-print false}}]}
  :aliases {"compile" ["do"
                       "clean"
                       ["cljsbuild" "once" "min"]
                       ["shell" "cp" "-r" "resources/public" "docs"]]})

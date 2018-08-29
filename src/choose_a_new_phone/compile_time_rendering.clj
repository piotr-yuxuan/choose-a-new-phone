(ns choose-a-new-phone.compile-time-rendering
  (:require [hiccup.core :as hiccup]
            [hiccup.page :as hiccup.page]
            [etaoin.api :as etaoin]))

(defn get-element-inner-html-el
  "Returns element's inner text by its identifier."
  [driver el]
  (etaoin/with-resp driver :get
    [:session (:session @driver) :element el :property :innerHTML]
    nil
    resp
    (-> resp :value)))

(defn get-element-inner-html
  "Returns element's inner HTML.
  For element `el` in `<div id=\"el\"><p class=\"foo\">hello</p></div>` it will
  be \"<p class=\"foo\">hello</p>\" string."
  [driver q]
  (get-element-inner-html-el driver (etaoin/query driver q)))

(def static-app-div-file
  "resources/static-app.html")

(defn static-app-div
  []
  (slurp static-app-div-file))

(defn refresh-static-app-div!
  "Refresh and return nil. Then you can get refreshed static app. Needs an active local figwheel instance or appropriate override."
  [{:keys [instance-uri
           driver-opts]
    :or {instance-uri "http://localhost:3449"
         driver-opts {:path-browser "/Applications/Firefox Developer Edition.app/Contents/MacOS/firefox"
                      :headless true}}}]
  (println "Needs an active local figwheel instance or appropriate override. Otherwise an exception will be thrown.")
  (let [inner-html (etaoin/with-firefox driver-opts driver
                     (etaoin/go driver instance-uri)
                     (etaoin/wait 15) ;; ugly WIP hack: wait for refresh to be finished
                     (get-element-inner-html driver {:id "app"}))]
    (spit static-app-div-file inner-html)
    nil))

(def google-tag-manager
  [:script {:type "text/javascript"
            :async :true
            :src "https://www.googletagmanager.com/gtag/js?id=UA-122750709-1"}])

(def google-analytics-tag
  [:script {:type "text/javascript"}
   "
window.dataLayer = window.dataLayer || [];
function gtag(){dataLayer.push(arguments);}
gtag('js', new Date());

gtag('config', 'UA-122750709-1');"])

(defn index-html-markup
  [{:keys [prod?] :as args}]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    (when prod? google-tag-manager)
    (when prod? google-analytics-tag)
    (hiccup.page/include-css "css/style.css")]
   [:body
    [:div {:id "app"}
     (static-app-div)]
    (hiccup.page/include-js "js/compiled/app.js")
    [:script {:type "text/javascript"}
     "choose_a_new_phone.core.init();"]]])

(defn render-index
  "Export static state for SEO and smooth display (WIP)"
  [{:keys [refresh?] :as args}]
  (let [index-html-file (if (contains? args :index-html)
                          (:index-html args)
                          "resources/public/index.html")]
    (when refresh?
      (refresh-static-app-div! args))
    (->> (index-html-markup args)
         hiccup.page/html5
         (spit index-html-file))))

(defn parse-args
  [args]
  (->> args
       (map #(->> %
                  (map clojure.edn/read-string)
                  vec))
       (into {})))

(defn cli
  "Export static state for SEO and smooth display (WIP)"
  [& {:as args}]
  (render-index (parse-args args)))

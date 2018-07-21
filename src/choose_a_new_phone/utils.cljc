(ns choose-a-new-phone.utils
  #?(:clj  (:require [yaml.core :as yaml])
     :cljs (:require [cljsjs.js-yaml])))

(defn yaml->map
  [s]
  #?(:clj  (yaml/parse-string s)
     :cljs (try (->> s
                     (.load js/jsyaml)
                     js->clj
                     clojure.walk/keywordize-keys)
                (catch :default _
                  ;; too fragile, some device descriptions aren't properly formatted
                  ))))

(ns choose-a-new-phone.utils
  #?(:clj  (:require [yaml.core :as yaml])
     :cljs (:require [cljsjs.js-yaml]))
  #?(:clj (:import (java.net URLEncoder))))

(defn yaml->map
  [s]
  ;; FIXME cljs parser doesn't accept as much as clj one.
  #?(:clj  (yaml/parse-string s)
     :cljs (try (->> s
                     (.load js/jsyaml)
                     js->clj
                     clojure.walk/keywordize-keys)
                (catch :default _
                  ;; too fragile, some device descriptions aren't properly formatted
                  ))))

(defn url-encode
  [arg]
  #?(:clj  (URLEncoder/encode (str arg) "UTF-8")
     :cljs (js/encodeURIComponent arg)))

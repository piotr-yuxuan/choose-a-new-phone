(ns choose-a-new-phone.effects
  (:require [re-frame.core :as re-frame]
            [re-frame.interop :as re-frame.interop]
            #?(:clj [clj-http.client :as client])))

(def ^:private protected-effects
  #{:db :cofx})

(re-frame/reg-event-fx
  ::dispatch
  (fn [_ [_ effect arg]]
    (when-not (protected-effects effect)
      (assoc {} effect arg))))

(re-frame/reg-fx
  ::println
  (fn [& args]
    (apply println args)))

;; ClojureScript code for ::http-xhrio

#?(:cljs
   (re-frame/reg-fx
     ::http-xhrio
     (fn [arg]
       (re-frame/dispatch [::dispatch :http-xhrio arg]))))

;; Clojure code for ::http-xhrio

#?(:clj
   (defn- multiple-requests?
     [arg]
     (or (not (map? arg))
         (list? arg)
         (vector? arg))))

#?(:clj
   (defmulti http-xhrio (fn [arg]
                          (if (multiple-requests? arg)
                            (:method (first arg))
                            (:method arg)))))

#?(:clj
   (re-frame/reg-fx
     ::http-xhrio
     http-xhrio))

#?(:clj
   (def default-headers
     {"Accept" "application/json, text/plain, */*"
      "Accept-Language" "en-US,en;q=0.5"
      "Connection" "keep-alive"
      "Pragma" "no-cache"
      "Cache-Control" "no-cache"}))

(def connection-pool-threads
  ;; arbitrary number, works on my machine
  30)

#?(:clj
   (def http-connection-manager
     (clj-http.conn-mgr/make-reuseable-async-conn-manager {:insecure? true ;; fuck you Oracle
                                                           :threads connection-pool-threads})))

#?(:clj
   (defn- http-xhrio-get-single-request
     [arg]
     (let [url (:uri arg)
           response-format (condp = (-> arg :response-format :description)
                             "JSON keywordize" {:as :json}
                             "JSON" {:as :json-string-keys}
                             "raw binary" {:as :string})
           query-params (when (contains? arg :params)
                          {:query-params (:params arg)})
           timeout (when (contains? arg :timeout)
                     {:socket-timeout (:timeout arg)
                      :conn-timeout (:timeout arg)})]
       (client/get url
                   (merge {:headers default-headers
                           :async? true
                           :connection-manager http-connection-manager}
                          response-format
                          query-params
                          timeout)
                   (fn [response] (when (contains? arg :on-success)
                                    (re-frame/dispatch (conj (:on-success arg) (:body response)))))
                   (fn [exception] (when (contains? arg :on-failure)
                                     (re-frame/dispatch (conj (:on-failure arg) exception))))))))

#?(:clj
   (defmethod http-xhrio :get
     [args]
     (if (multiple-requests? args)
       (do (http-xhrio-get-single-request (first args))
           (when-let [requests (seq (rest args))]
             (http-xhrio requests)))
       (http-xhrio-get-single-request args))))

;; End of code for ::http-xhrio

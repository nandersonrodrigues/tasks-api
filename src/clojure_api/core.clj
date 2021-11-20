(ns clojure-api.core
  (:require [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]))

(defn func-hello [request]
  {:status 200 :body (str "Hello World " (get-in request [:query-params :name] "Everybody"))})

(def routes (route/expand-routes
             #{["/hello" :get func-hello :route-name :hello-world]}))

(def service-map {::http/routes routes
                  ::http/port 8081
                  ::http/type :jetty
                  ::http/join? false})

(http/start (http/create-server service-map))
(println "started server http")

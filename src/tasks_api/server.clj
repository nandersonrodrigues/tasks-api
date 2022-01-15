 (ns tasks-api.server
   (:require [io.pedestal.http :as http]
             [io.pedestal.interceptor :as i]
             [tasks-api.controller :as controller]
             [io.pedestal.test :as test]))



(def service-map {::http/routes controller/list-routes
                  ::http/port 8081
                  ::http/type :jetty
                  ::http/join? false})

(def service-map-with-interceptor
  (-> service-map
      (http/default-interceptors)
      (update ::http/interceptors conj (i/interceptor controller/db-interceptor))))

(defonce server (atom nil))

(defn start-server []
  (reset! server (http/start (http/create-server service-map-with-interceptor))))

(defn stop-server []
  (http/stop @server))

(defn restart-server []
  (stop-server)
  (start-server))

(defn test-request [verb url]
  (test/response-for (::http/service-fn @server) verb url))

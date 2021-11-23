 (ns clojure-api.server
   (:require [io.pedestal.http.route :as route]
             [io.pedestal.http :as http]
             [io.pedestal.test :as test]
             [clojure-api.database :as database]
             [io.pedestal.interceptor :as i]))

(defn assoc-store [context]
  (update context :request assoc :store database/store))

(def db-interceptor
  {:name :db-interceptor
   :enter assoc-store})

(defn list-tasks [request]
  {:status 200 :body @(:store request)})

(defn create-task-map [uuid name status]
  {:uuid uuid :name name :status status})

; {id {task_id task_name task_status}}
(defn create-task [request]
  (let
   [uuid (java.util.UUID/randomUUID)
    name (get-in request [:query-params :name])
    status (get-in request [:query-params :status])
    task (create-task-map uuid name status)
    store (:store request)]
    (swap! store assoc uuid task)
    {:status 200 :body {:mensagem "tarefa registrada com sucesso"
                        :task task}}))

(defn delete-task [request]
  (let [store (:store request)
        task-id (get-in request [:path-params :id])
        task-id-uuid (java.util.UUID/fromString task-id)]
    (swap! store dissoc task-id-uuid)
    {:status 200 :body {:message "Removido com sucesso"}}))

(defn update-task [request]
  (let [task-id (get-in request [:path-params :id])
        task-id-uuid (java.util.UUID/fromString task-id)
        name (get-in request [:query-params :name])
        status (get-in request [:query-params :status])
        task (create-task-map task-id-uuid name status)
        store (:store request)]
    (swap! store assoc task-id-uuid task)
    {:status 200 :body {:message "Tarefa atualizada com sucesso!"}}))
   

(defn func-hello [request]
  {:status 200 :body (str "Hello World " (get-in request [:query-params :name] "Everybody"))})

(def routes (route/expand-routes
             #{["/hello" :get func-hello :route-name :hello-world]
               ["/task" :post [create-task] :route-name :create-task]
               ["/task" :get [list-tasks] :route-name :list-tasks]
               ["/task/:id" :delete [delete-task] :route-name :delete-task]
               ["/task/:id" :patch [update-task] :route-name :update-task]}))

(def service-map {::http/routes routes
                  ::http/port 8081
                  ::http/type :jetty
                  ::http/join? false})

(def service-map-with-interceptor (-> service-map
                                       (http/default-interceptors)
                                       (update ::http/interceptors conj (i/interceptor db-interceptor))))

(defonce server (atom nil))

(defn start-server []
  (reset! server (http/start (http/create-server service-map-with-interceptor))))

(defn test-request [verb url]
  (test/response-for (::http/service-fn @server) verb url))

(defn stop-server []
  (http/stop @server))

(defn restart-server []
  (stop-server)
  (start-server))


(start-server)
(restart-server)
(stop-server)

(println (test-request :get "/hello?name=nanderson"))
(println (test-request :post "/task?name=nanderson&status=pendente"))
(println (test-request :post "/task?name=maria&status=ok"))
(println (test-request :get "/task"))
(println (test-request :delete "/task/7dc35f3c-7dea-44cf-9135-9819ac8828ce"))
(println (test-request :patch "/task/8faa1230-326f-4acd-9272-da08facffb05?name=Joao&status=pendente"))

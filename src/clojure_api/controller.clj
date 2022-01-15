(ns clojure-api.controller
  (:require [clojure-api.database :as database]
            [io.pedestal.http.route :as route]))



(defn assoc-store [context]
  (update context :request assoc :store database/store))

(def db-interceptor
  {:name :db-interceptor
   :enter assoc-store})

(defn list-tasks [request]
  {:status 200 
   :body @(:store request)})

(defn create-task-map [uuid name status]
  {:uuid uuid 
   :name name 
   :status status})

; {id {task_id task_name task_status}}
(defn create-task [request]
  (let
   [uuid   (java.util.UUID/randomUUID)
    name   (get-in request [:query-params :name])
    status (get-in request [:query-params :status])
    task   (create-task-map uuid name status)
    store  (:store request)]
    (swap! store assoc uuid task)
    {:status 200 
     :body {:mensagem "task successfully registered!"
            :task task}}))

(defn delete-task [request]
  (let 
   [store        (:store request)
    task-id      (get-in request [:path-params :id])
    task-id-uuid (java.util.UUID/fromString task-id)]
    (swap! store dissoc task-id-uuid)
    {:status 200 
     :body {:message "task successfully deleted!"}}))

(defn update-task [request]
  (let 
   [task-id      (get-in request [:path-params :id])
    task-id-uuid (java.util.UUID/fromString task-id)
    name         (get-in request [:query-params :name])
    status       (get-in request [:query-params :status])
    task         (create-task-map task-id-uuid name status)
    store        (:store request)]
    (swap! store assoc task-id-uuid task)
    {:status 200 
     :body {:message "task successfully updated!"}}))


(defn func-hello [request]
  {:status 200 
   :body (str "Hello World " (get-in request [:query-params :name] "Everybody"))})

(def list-routes (route/expand-routes
             #{["/hello" :get func-hello :route-name :hello-world]
               ["/task" :post [create-task] :route-name :create-task]
               ["/task" :get [list-tasks] :route-name :list-tasks]
               ["/task/:id" :delete [delete-task] :route-name :delete-task]
               ["/task/:id" :patch [update-task] :route-name :update-task]}))
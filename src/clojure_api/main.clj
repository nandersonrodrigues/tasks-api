(ns clojure-api.main
  (:require 
            [clojure-api.server :as server]))

(defn start-api []
  (server/start-server)
  ;(server/stop-server)
  ;(server/restart-server)

  (server/test-request :get "/hello?name=nanderson")
  (server/test-request :post "/task?name=nanderson&status=pendente")
  (server/test-request :post "/task?name=maria&status=ok")
  (server/test-request :get "/task")
  (server/test-request :delete "/task/7dc35f3c-7dea-44cf-9135-9819ac8828ce")
  (server/test-request :patch "/task/8faa1230-326f-4acd-9272-da08facffb05?name=Joao&status=pendente"))



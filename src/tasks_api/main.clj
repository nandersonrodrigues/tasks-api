(ns tasks-api.main
  (:require
   [tasks-api.server :as server]
   [tasks-api.utils :refer [print-pretty-json]]))

(defn start-api []
  (server/start-server)
  ;(server/stop-server)
  ;(server/restart-server)

  (print-pretty-json (server/test-request :get "/hello?name=maria"))
  (print-pretty-json (server/test-request :post "/task?name=play&status=pending"))
  (print-pretty-json (server/test-request :post "/task?name=eat&status=ok"))
  (print-pretty-json (server/test-request :get "/task"))
  (print-pretty-json (server/test-request :delete "/task/efa1b65e-8b37-4b94-8aa3-38182e21a87a"))
  (print-pretty-json (server/test-request :patch "/task/e6669bcd-cfa8-4902-9a94-3f4eed42c006?name=run&status=pending"))
  )



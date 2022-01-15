(ns tasks-api.utils 
  (:require [cheshire.core :refer [generate-string]]))

(defn print-pretty-json [json]
  (print (generate-string json {:pretty true}) "\n"))

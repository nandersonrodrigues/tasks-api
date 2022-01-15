(ns clojure-api.utils 
  (:require [cheshire.core :refer [generate-string]]))

;generate-string {:foo "bar" :baz {:eggplant [1 2 3]}} {:pretty true}
(defn print-pretty-json [json]
  (print (generate-string json {:pretty true}) "\n"))
(ns app
  (:require [rpub.main :as main]
            [rpub.plugins.external-editing]
            [rpub.plugins.starter-theme]))

(defn -main [& _]
  (main/start!))

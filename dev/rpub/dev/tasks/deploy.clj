(ns rpub.dev.tasks.deploy
  {:no-doc true}
  (:require [deps-deploy.deps-deploy :as deps-deploy]
            [rpub.dev.tasks :as dev-tasks]))

(def pom-file "target/classes/META-INF/maven/dev.rpub/rpub/pom.xml")
(def jar (format "target/rpub-%s.jar" @dev-tasks/version))

(defn deploy [_]
  (deps-deploy/deploy
    {:installer :remote
     :sign-releases? true
     :pom-file pom-file
     :artifact jar}))

(defn install [_]
  (deps-deploy/deploy
    {:installer :local
     :pom-file pom-file
     :artifact jar}))

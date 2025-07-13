(ns rpub.model.themes
  {:no-doc true}
  (:require [medley.core :as medley]
            [rpub.model.common :as common]))

^:clj-reload/keep
(defprotocol Model
  (-get-themes [model opts])
  (-create-theme! [model theme])
  (-update-theme! [model theme])
  (-delete-theme! [model theme]))

(defn- coerce [model]
  (get-in model [:models :themes] model))

(defn get-themes [model opts]
  (-get-themes (coerce model) opts))

(defn create-theme! [model theme]
  (-create-theme! (coerce model) theme))

(defn update-theme! [model theme]
  (-update-theme! (coerce model) theme))

(defn delete-theme! [model theme]
  (-delete-theme! (coerce model) theme))

(defn active-theme
  ([req] (active-theme req nil))
  ([{:keys [settings themes] :as _req} custom-themes]
   (let [theme-name-value (get-in settings [:theme-name :value])
         theme (medley/find-first #(= (:label %) theme-name-value)
                                  (concat themes custom-themes))]
     (or theme {:label theme-name-value}))))

(defn custom-theme? [theme]
  (get-in theme [:value :html-template]))

(defn ->theme [& {:keys [id label value current-user new] :as _opts}]
  (cond-> {:id (or id (random-uuid))
           :label label
           :value value}
    current-user (common/add-metadata current-user)
    new (assoc :new new)))

(defmulti ->model :db-type)

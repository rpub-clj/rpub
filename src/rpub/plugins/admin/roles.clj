(ns rpub.plugins.admin.roles
  (:require [camel-snake-kebab.core :as csk]
            [rads.inflections :as inflections]))

(defn allowed? [role permission]
  (or (and (keyword? (:permissions role))
           (= (:permissions role) :all))
      (and (set? (:permissions role))
           (contains? (:permissions role) permission))))

(defn all-permissions [{:keys [roles content-types]}]
  (for [resource (concat [:users] (map :slug content-types))
        action [:create :edit]]
    (let [permission {:resource resource, :action action}
          allowed-roles (map :label (filter #(allowed? % permission) roles))]
      (-> permission
          (assoc :roles allowed-roles)
          (assoc :permission-key
                 (keyword (str (csk/->kebab-case-string (:action permission))
                               "-"
                               (inflections/singular (csk/->kebab-case-string (:resource permission))))))))))

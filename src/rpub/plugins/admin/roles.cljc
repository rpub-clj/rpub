(ns rpub.plugins.admin.roles
  #?(:clj (:require [buddy.auth :as buddy-auth]
                    [camel-snake-kebab.core :as csk]
                    [rads.inflections :as inflections])))

(defn- allowed-for-role? [role permission]
  (or (and (keyword? (:permissions role))
           (= (:permissions role) :all))
      (and (set? (:permissions role))
           (contains? (:permissions role) permission))))

(defn- allowed-for-user? [user permission]
  (some #(allowed-for-role? % permission) (:roles user)))

(defn allowed? [user-or-role permission]
  (if (:roles user-or-role)
    (allowed-for-user? user-or-role permission)
    (allowed-for-role? user-or-role permission)))

#?(:clj
   (do
     (def defaults
       {:resources [:users :roles :content-types]
        :actions [:create :edit]})

     (defn assert-allowed [user permission]
       (when-not (some #(allowed? % permission) (:roles user))
         (buddy-auth/throw-unauthorized
           {:user (select-keys user [:id :roles])
            :permission permission})))

     (defn all-permissions [{:keys [roles content-types]}]
       (for [resource (concat (:resources defaults) (map :slug content-types))
             action (:actions defaults)]
         (let [permission {:resource resource, :action action}
               allowed-roles (map :label (filter #(allowed? % permission) roles))]
           (-> permission
               (assoc :roles allowed-roles)
               (assoc :permission-key
                      (keyword (str (csk/->kebab-case-string (:action permission))
                                    "-"
                                    (inflections/singular (csk/->kebab-case-string (:resource permission))))))))))))

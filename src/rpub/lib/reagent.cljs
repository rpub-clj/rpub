(ns rpub.lib.reagent
  {:no-doc true}
  (:require ["react" :as react]))

(defn- parse-element [form]
  (if (map? (second form))
    {:el (first form), :attrs (second form), :children (drop 2 form)}
    {:el (first form), :children (rest form)}))

(defn- ->clj-props [js-props]
  (reduce (fn [m [k v]] (assoc m (keyword k) v))
          {}
          (js/Object.entries js-props)))

(declare as-element)

(defn Adapter [js-props]
  (let [clj-props (-> (->clj-props js-props) (dissoc :__el))]
    (as-element ((.-__el js-props) clj-props))))

(defn as-element [form]
  (cond
    (string? form) form

    (and (map? form) (not (record? form)))
    (reduce (fn [acc [k v]]
              (aset acc (name k) v)
              acc)
            (js-obj)
            form)

    (vector? form)
    (let [{:keys [el attrs children]} (parse-element form)
          children' (into-array (map as-element children))]
      (cond
        (keyword? el) (react/createElement
                        (name el)
                        (some-> attrs as-element)
                        children')
        (fn? el) (react/createElement
                   Adapter
                   (as-element (assoc attrs :__el el))
                   children')))

    (sequential? form)
    (into-array (map as-element form))

    :else form))

(defn reactify-component [reagent-component]
  (fn [props]
    (as-element [reagent-component props])))

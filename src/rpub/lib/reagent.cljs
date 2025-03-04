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
          el' (cond
                (keyword? el) (name el)
                (fn? el) (fn [js-props]
                           (let [clj-props (->clj-props js-props)]
                             (as-element (el clj-props)))))
          attrs' (some-> attrs as-element)
          children' (into-array (map as-element children))]
      (react/createElement el' attrs' children'))

    (sequential? form)
    (into-array (map as-element form))

    :else form))

(defn reactify-component [reagent-component]
  (fn [props]
    (as-element [reagent-component props])))

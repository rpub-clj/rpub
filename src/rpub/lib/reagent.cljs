(ns rpub.lib.reagent
  {:no-doc true}
  (:require ["react" :as react]
            [clojure.string :as str]))

(defn- add-classes [attrs classes]
  (let [classes' (if (:class attrs)
                   (conj classes (:class attrs))
                   classes)]
    (str/join " " classes')))

(defn- parse-element [form]
  (let [parsed (if (map? (second form))
                 (let [[el attrs & children] form]
                   {:el el, :attrs attrs, :children children})
                 (let [[el & children] form]
                   {:el el, :children children}))]
    (if-not (keyword? (:el parsed))
      parsed
      (let [[el' & classes] (str/split (name (:el parsed)) #"\.")]
        (cond-> (assoc parsed :el el')
          (seq classes) (update-in [:attrs :class] add-classes classes))))))

(defn- ->clj-props [js-props]
  (reduce (fn [m [k v]] (assoc m (keyword k) v))
          {}
          (js/Object.entries js-props)))

(declare as-element)

(defn Adapter [js-props]
  (let [clj-props (-> (->clj-props js-props) (dissoc :__el))]
    (as-element ((.-__el js-props) clj-props))))

(defn- string->el [s] s)

(defn- map->el [m]
  (reduce (fn [acc [k v]]
            (aset acc (name k) v)
            acc)
          (js-obj)
          m))

(defn- keyword-vector->el [form]
  (let [{:keys [el attrs children]} (parse-element form)
        children' (as-element children)
        attrs' (some-> attrs as-element)]
    (react/createElement el attrs' children')))

(defn- fn-vector->el [form]
  (let [{:keys [el attrs children]} (parse-element form)
        children' (as-element children)
        attrs' (as-element (assoc attrs :__el el))]
    (react/createElement Adapter attrs' children')))

(defn- sequential->el [xs]
  (into-array (map as-element xs)))

(defn as-element [form]
  (cond
    (string? form) (string->el form)
    (and (map? form) (not (record? form))) (map->el form)
    (and (vector? form) (keyword? (first form))) (keyword-vector->el form)
    (and (vector? form) (fn? (first form))) (fn-vector->el form)
    (sequential? form) (sequential->el form)
    :else form))

(defn reactify-component [reagent-component]
  (fn [props]
    (as-element [reagent-component props])))

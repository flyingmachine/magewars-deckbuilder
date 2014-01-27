(ns magewars-deckbuilder.filtering
  (:require-macros
   [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require [magewars-deckbuilder.utils :refer [mapval]]
            [tailrecursion.javelin :as j :refer [cell]]))

(def attribute-filter-types
  {:type :keyword
   :subtypes :keyword
   :speed :keyword
   :armor :number
   :defenses :boolean
   :life :number
   :channeling :number
   :traits :vset
   :attack-dice :number
   :damage-types :keyword
   :effects :vset
   :ranged-melee :keyword
   :attack-speed :keyword
   :targets :vset
   :secondary-targets :vset
   :slot :vset
   :school :keyword
   :level :number})

;; TODO some better general way to map over map value
(def filter-type-attributes
  (mapval #(set (map first %)) (group-by second attribute-filter-types)))

(defn empty-filters
  [attrs]
  (into {} (map (fn [[a]] [a #{}]) attrs)))

(defn only-non-empty
  [filters]
  (filter #(not (empty? (second %))) filters))

(defn leaves
  "In sets of [x] [x y], return only [x y]. Assumes only two level"
  [s]
  (:leaves
   (reduce (fn [final x]
             (if ((:flat final) (first x))
               final
               {:flat (into (:flat final) x)
                :leaves (conj (:leaves final) x)}))
           {:flat #{}
            :leaves #{}}
           (reverse (sort-by :count s)))))

(defn prep-filters
  [filters]
  (merge filters
         (mapval
          (fn [v] (leaves v))
          (select-keys filters (:vset filter-type-attributes)))))

(defn filter-card
  [filters card]
  (every? (fn [[attribute values]]
            (not-empty
             (clojure.set/intersection
              values
              (get-in card [:search attribute]))))
          (only-non-empty filters)))

(defn filter-cards
  [cards filters]
  (filter (partial filter-card (prep-filters filters)) cards))

(defn filtered-cards
  "filters should be a chell"
  [cards filters]
  (cell= (sort-by :name (filter-cards cards filters))))

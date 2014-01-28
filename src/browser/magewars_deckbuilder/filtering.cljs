(ns magewars-deckbuilder.filtering
  (:require [magewars-deckbuilder.utils :refer [mapval]]))

(set! cljs.core/*print-fn* #(.log js/console %))
;; TODO put this in cards, consolidate filter-options to go in cards
;; and be returned as big state thing
(def attribute-filter-types
  {:type :keyword
   :subtypes :keyword
   :casting-cost :number
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
   :slots :vset
   :school :keyword
   :level :number})

(def filter-type-attributes
  (mapval #(set (map first %)) (group-by second attribute-filter-types)))

(defn empty-filters
  [attrs]
  (mapval (constantly #{}) attrs))

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

(defn filter-match
  [attr valset search]
  (->> (attr search)
       (clojure.set/intersection valset)
       (not-empty)))

(defn filter-card
  [filters card]
  (every? (fn [[attribute values]]
            (filter-match attribute values (:search card)))
          (only-non-empty filters)))

(defn filter-cards
  [cards filters]
  (filter (partial filter-card (prep-filters filters)) cards))

(defn filter-options
  [cards]
  (let [opts (reduce (partial merge-with into)
                     (map :search cards))]
    (merge opts
           (mapval
            (fn [v]
              (mapval
               (fn [w] (set (flatten (map rest w))))
               (group-by first v)))
            (select-keys opts (:vset filter-type-attributes))))))

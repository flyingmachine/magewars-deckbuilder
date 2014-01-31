(ns magewars-deckbuilder.filtering
  (:require [magewars-deckbuilder.utils :refer [mapval]]
            [clojure.set :as s]))

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
  "For vset filters, only keep leaves. Otherwise set intersection
  returns more results than it should."
  [filters]
  (merge filters
         (mapval
          (fn [v] (leaves v))
          (select-keys filters (:vset filter-type-attributes)))))

(defn filter-match
  "Does a card's attribute intersect with the given filter val set?"
  [attr fvals card]
  (->> (attr card)
       (s/intersection fvals)
       (not-empty)))

(defn filter-card
  [filters card]
  (every? (fn [[attribute values]]
            (filter-match attribute values (:search card)))
          (only-non-empty filters)))

(defn filter-cards
  [cards filters]
  (filter (partial filter-card (prep-filters filters)) cards))

(defn filter-vals
  [cards]
  (->> (map :search cards)
       (reduce (partial merge-with into))
       (mapval #(set (filter identity %)))))

(defn filter-options
  [cards]
  (let [fvals (filter-vals cards)]
    (merge fvals
           (mapval
            (fn [v]
              (mapval
               (fn [w] (set (filter identity (flatten (map rest w)))))
               (group-by first v)))
            (select-keys fvals (:vset filter-type-attributes))))))

(defn filter-cards-indexed
  [cards filters index]
  (apply s/intersection
         cards
         (map (fn [[attr fvals]]
                (apply s/union (map #(get index [attr %]) fvals)))
              (only-non-empty filters))))

(defn card-index
  [cards filters]
  (reduce
   (fn [ci [attr fvals]]
     (reduce
      (fn [g fval]
        (assoc g [attr fval] (set (filter-cards cards {attr #{fval}}))))
      ci
      fvals))
   {}
   filters))

;; filters: {:attr set :attr2 set}
;; filter: (attr fvals)
;; attr: :type
;; fvals: set
;; target: cardvals

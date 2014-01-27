(ns magewars-deckbuilder.cards
  (:require [clojure.java.io :as io]))

;; reading static card sets
(defn slurp-resource
  [path]
  (-> path
      io/resource
      slurp))

(defn read-resource
  [path]
  (-> path
      slurp-resource
      read-string))

(defn read-edn
  [& pieces]
  (->> pieces
       reverse
       (map name)
       (into [".edn"])
       reverse
       (clojure.string/join "")
       read-resource))

;; "unpack" cards
(def common-attributes
  {:attacks      {:type :attack
                  :speed :quick}
   :conjurations {:type :conjuration
                  :targets #{:zone}
                  :speed :quick
                  :range [0 1]}
   :creatures    {:type :creature
                  :targets #{:zone}
                  :speed :slow}
   :enchantments {:type :enchantment
                  :speed :quick}
   :equipment    {:type :equipment
                  :targets #{[:creature :mage]}
                  :speed :quick
                  :range [0 2]}
   :incantations {:type :incantation}})

(def transformations
  {:attacks [(fn [x]
               (let [attack-keys [:dice :damage-type :effects :traits]]
                 (merge (apply dissoc x attack-keys)
                        {:attacks #{(select-keys x (conj attack-keys :range))}})))]})

(defn count-in-sets
  [card sets]
  (reduce + (map #(get % (:name card) 0) sets)))
(defn merge-count
  [card sets]
  (merge card {:count (count-in-sets card sets)}))

;; create a search view
(defn vset
  [val]
  (reduce (fn [final x]
            (if (keyword? x)
              (conj final [x])
              (let [[head & tail] x]
                (conj (reduce conj final (map (fn [y] [head y]) tail))
                      [head]))))
        #{}
        val))

(defn ->set [x] (if (set? x) x #{x}))

(defn with-attacks
  [card indexer attr]
  (reduce into
          (indexer (attr card))
          (map (comp indexer attr) (:attacks card))))

(defn attack-set
  [card attr]
  (set (map attr (:attacks card))))

(defn school-pair
  [pair]
  {:school #{(first pair)}
   :level #{(second pair)}})

(defn school-level
  [{school :school}]
  (if (keyword? (first school))
    (school-pair school)
    (reduce (partial merge-with into) (map school-pair school))))

(defn card-views
  [card]
  {:name (:name card)
   :display card
   :search (merge
            (reduce into
                    (map (fn [[k v]] {k (->set v)})
                         (select-keys card
                                      [:type :subtypes :speed :armor
                                       :defenses :life :channeling])))
            {:traits (with-attacks card vset :traits)
             :attack-dice (attack-set card :dice)
             :damage-types (attack-set card :damage-type)
             :effects (with-attacks card vset :effects)
             :ranged-melee (attack-set card :ranged-melee)
             :attack-speed (attack-set card :speed)
             :targets (with-attacks card vset :targets)
             :secondary-targets (with-attacks card vset :secondary-targets)
             :slot (vset (:slot card))}
            (school-level card))})

(defn cards-by-type
  [card-type sets]
  (->> (read-edn "cards/" card-type)
       (map (fn [card]
              (-> (card-type common-attributes)
                  (merge card)
                  ((apply comp (card-type transformations [])))
                  (card-views)
                  (merge-count sets))))
       (into #{})))


(defn card-set
  [set-name]
  (read-edn "sets/" set-name))

(def cards
  (->> (keys common-attributes)
       (map #(cards-by-type % [(card-set :core)]))
       (reduce into)))



;; Filter testing

(def attribute-filter
  {:type #{:enchantment :equipment}
   :targets #{[:creature :mage]}})

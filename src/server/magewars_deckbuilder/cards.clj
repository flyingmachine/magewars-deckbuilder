(ns magewars-deckbuilder.cards
  (:require [clojure.java.io :as io]))

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
                  :targets #{:mage}
                  :speed :quick
                  :range [0 2]}
   :incantations {:type :incantation}})

(def transformations
  {:attacks [(fn [x]
               (let [attack-keys [:dice :damage-type :effects :traits]]
                 (merge (apply dissoc x attack-keys)
                        {:attacks #{(select-keys x (conj attack-keys :range))}})))]})

;;
(defn card-set
  [set-name]
  (read-edn "sets/" set-name))

(defn cards-by-type
  [card-type common-attributes transformations sets]
  (->> (read-edn "cards/" card-type)
       (map (fn [x]
              (let [card (merge (card-type common-attributes) x)]
                (if-let [t (card-type transformations)]
                  ((apply comp t) card)
                  card))))
       (into #{})))

(def cards
  (->> (keys common)
       (map #(cards-by-type % common-attributes transformations []))
       (reduce into)))

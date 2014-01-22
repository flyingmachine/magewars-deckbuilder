(ns magewars-deckbuilder.deck
  (:require-macros
   [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin :as j :refer [cell]]))

;; TODO derive attributes from cards
(def attributes
  {:school #{:chaos :divine :nature :elemental}
   :type #{:creature :spell :equipment}})

(defc deck
  #{{:name "minotaur"
     :school :chaos
     :cost 12
     :type :creature
     :count 2}
    {:name "heal"
     :school :divine
     :cost 3
     :type :spell
     :count 4}})

(defc base-set
  #{{:name "magic wand"
     :school :elemental
     :cost 8
     :type :equipment
     :count 5}
    {:name "fox"
     :school :nature
     :cost 3
     :type :creature
     :count 1}
    {:name "panther"
     :school :nature
     :cost 12
     :type :creature
     :count 2}
    {:name "fireball"
     :school :chaos
     :cost 6
     :type :spell
     :count 2}})

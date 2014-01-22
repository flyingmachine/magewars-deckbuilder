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
  #{{:title "minotaur"
     :school :chaos
     :cost 12
     :type :creature
     :count 2}
    {:title "heal"
     :school :divine
     :cost 3
     :type :spell
     :count 4}})

(defc base-set
  #{{:title "magic wand"
     :school :elemental
     :cost 8
     :type :equipment
     :count 5}
    {:title "fox"
     :school :nature
     :cost 3
     :type :creature
     :count 1}
    {:title "panther"
     :school :nature
     :cost 12
     :type :creature
     :count 2}
    {:title "fireball"
     :school :chaos
     :cost 6
     :type :spell
     :count 2}})

(ns magewars-deckbuilder.deck
  (:require-macros
   [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin :as j :refer [cell]]))

;; TODO derive attributes from cards
(def attributes
  {:school #{:chaos :divine :nature :elemental}
   :type #{:creature :spell :equipment}})

(def cards
  #{{:title "minotaur"
     :school :chaos
     :cost 12
     :type :creature}
    {:title "heal"
     :school :divine
     :cost 3
     :type :spell}
    {:title "magic wand"
     :school :elemental
     :cost 8
     :type :equipment}
    {:title "fox"
     :school :nature
     :cost 3
     :type :creature}
    {:title "panther"
     :school :nature
     :cost 12
     :type :creature}
    {:title "fireball"
     :school :chaos
     :cost 6
     :type :spell}})

(def card-map
  (reduce (fn [r [k v]] (assoc r k (first v))) {} (group-by :title cards)))

(defc deck
  {"minotaur" 1
   "heal" 2})

(defc base-set
  {"magic wand" 2
   "fox" 3
   "panther" 1
   "fireball" 4})

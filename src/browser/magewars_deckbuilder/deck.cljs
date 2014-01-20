(ns magewars-deckbuilder.deck
  (:require-macros
   [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin :as j :refer [cell]]))


(def attributes
  {:school #{:chaos :divine :nature :elemental}
   :type #{:creature :spell :equipment}})

(defc deck
  #{{:title "minotaur"
     :school :chaos
     :cost 12
     :type :creature}
    {:title "heal"
     :school :divine
     :cost 3
     :type :spell}})

(defc base-set
  #{{:title "magic wand"
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

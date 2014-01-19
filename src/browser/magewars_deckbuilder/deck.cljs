(ns magewars-deckbuilder.deck
  (:require-macros
   [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin :as j :refer [cell]]))


(def attributes
  {:schools [:chaos :divine :nature]
   :types [:creature :spell]})

(defc deck [{:title "minotaur"
             :school :chaos
             :cost 12
             :type :creature}
            {:title "heal"
             :school :divine
             :cost 3
             :type :spell}])

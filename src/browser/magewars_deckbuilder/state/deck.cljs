(ns magewars-deckbuilder.state.deck
  (:require-macros
   [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin :as j :refer [cell]]
   [tailrecursion.castra  :as c :refer [mkremote]]))

(defc deck {})
(defc error nil)
(def get-state (mkremote 'magewars-deckbuilder.api.deck/get-state deck error (cell nil)))

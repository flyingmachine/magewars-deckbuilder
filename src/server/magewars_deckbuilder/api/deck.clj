(ns magewars-deckbuilder.api.deck
  (:refer-clojure :exclude  [defn swap!])
  (:require [tailrecursion.castra :refer [defn ex error *session*]]
            [magewars-deckbuilder.cards :refer [cards]]))


(defn get-state
  [& _]
  {:rpc []}
  cards)

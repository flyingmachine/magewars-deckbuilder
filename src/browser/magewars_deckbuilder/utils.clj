(ns magewars-deckbuilder.utils)

(defn mapval
  [f m]
  (if (empty? m)
    m
    (reduce into
            (map (fn [[k v]] {k (f v)}) m))))

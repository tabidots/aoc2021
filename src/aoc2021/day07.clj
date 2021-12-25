(ns aoc2021.day07
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(comment
 "Day 7: The Treachery of Whales")

;; TODO: Add docstrings

(def sample-input
  [16 1 2 0 4 2 7 1 2 14])

(def puzzle-input
  (let [raw (-> "../resources/day07.txt" io/resource io/reader slurp (s/split #","))]
    (map read-string raw)))

(defn diff
  [a b]
  (Math/abs (- a b)))

(defn cost-at-position
  [pos data]
  (apply + (map (partial diff pos) data)))

(defn part-1
  [data]
  (let [max-pos (apply max data)]
    (apply min
      (pmap #(cost-at-position % data) (range max-pos)))))

(defn dynamic-diff
  [a b]
  (reduce + (range (inc (diff a b)))))

(defn dynamic-cost-at-position
  [pos data]
  (apply + (map (partial dynamic-diff pos) data)))

(defn part-2
  [data]
  (let [max-pos (apply max data)]
    (apply min
      (pmap #(dynamic-cost-at-position % data) (range max-pos)))))

(time
 (do
   (println "Day 7: The Treachery of Whales")
   (println "[Part 1] Sample:" (part-1 sample-input) "Puzzle:" (part-1 puzzle-input))
   (println "[Part 2] Sample:" (part-2 sample-input) "Puzzle:" (part-2 puzzle-input))))

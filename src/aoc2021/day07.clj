(ns aoc2021.day07
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(comment
 "Day 7: The Treachery of Whales")

(comment
 "Originally solved via brute force (15s). Took solution from megathread and
 runtime is now ~40ms using median, mean, and triangular numbers.")
;; TODO: Add docstrings. Why does the mean/median produce the lowest cost?

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

(defn median
  "Returns the median of a coll of numbers, without rounding."
  [coll]
  (let [items (count coll)]
    (if (odd? items)
      (nth (sort coll) (int (Math/ceil (/ items 2))))
      (let [[head tail] (split-at (/ items 2) (sort coll))
            lower       (last head)
            higher      (first tail)]
        (/ (+ lower higher)
           2)))))

(defn floor-ceil
  [x]
  [(int (Math/floor x)) (int (Math/ceil x))])

(defn part-1
  [data]
  (->> (floor-ceil (median data))
       (map #(cost-at-position % data))
       (apply min)))

(defn dynamic-diff
  ;; TODO: Use triangular numbers here
  [a b]
  (let [n (diff a b)]
    (/ (* n (inc n))
       2)))
  ;(reduce + (range (inc (diff a b)))))

(defn dynamic-cost-at-position
  [pos data]
  (apply + (map (partial dynamic-diff pos) data)))

(defn mean
  "Returns the mean of a coll of numbers (without rounding)."
  [coll]
  (/ (apply + coll)
     (count coll)))

(defn part-2
  [data]
  (->> (floor-ceil (mean data))
       (map #(dynamic-cost-at-position % data))
       (apply min)))

(time
 (do
   (println "Day 7: The Treachery of Whales")
   (println "[Part 1] Sample:" (part-1 sample-input) "Puzzle:" (part-1 puzzle-input))
   (println "[Part 2] Sample:" (part-2 sample-input) "Puzzle:" (part-2 puzzle-input))))

(ns aoc2021.day06
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(comment
 "Day 6: Lanternfish")

(def sample-input
  (->> (s/split "3,4,3,1,2" #",")
       (map read-string)
       frequencies))

(def puzzle-input
  (let [raw (-> "../resources/day06.txt" io/resource io/reader slurp (s/split #","))]
    (frequencies (mapv read-string raw))))

(defn reset
  "Decrements a lanternfish's birth timer, or resets it to 6 after giving birth."
  [timer]
  (if (zero? timer) 6
    (dec timer)))

(defn one-day
  "Advances one day in lanternfish evolution."
  [fishes]
  (let [births (fishes 0) ;; all fish with timer 0 will give birth
        result (apply merge-with +
                 (for [[timer num-fish] fishes]
                   {(reset timer) num-fish}))]
    (cond-> result
      births (assoc 8 births))))

(defn elapse
  "Advances the given number of days in lanternfish evolution."
  [fishes days]
  (-> (iterate one-day fishes)
      (nth days)))

(defn total-fish
  "Counts the number of fish present at a given point in time."
  [fishes]
  (reduce + (vals fishes)))

(defn solve
  [input days]
  (total-fish (elapse input days)))

(time
 (do
   (println "Day 6: Lanternfish")
   (println "[Part 1] Sample:" (solve sample-input 80) "Puzzle:" (solve puzzle-input 80))
   (println "[Part 2] Sample:" (solve sample-input 256) "Puzzle:" (solve puzzle-input 256))))

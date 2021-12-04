(ns aoc2021.day04
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

;; Set up the example

(def sample-calls
  [7 4 9 5 11 17 23 2 0 14 21 24 10 16 13 6 15 25 12 22 18 20 8 19 3 26 1])

(def sample-boards
  [[[22 13 17 11  0]
    [ 8  2 23  4 24]
    [21  9 14 16  7]
    [ 6 10  3 18  5]
    [ 1 12 20 15 19]]

   [[ 3 15  0  2 22]
    [ 9 18 13 17  5]
    [19  8  7 25 23]
    [20 11 10 24  4]
    [14 21 16 12  6]]

   [[14 21 17 24  4]
    [10 16 15  9 19]
    [18  8 23 26 20]
    [22 11 13  6  5]
    [ 2  0 12  3  7]]])

(def sample-init-state
  {:called   '()
   :uncalled sample-calls
   :boards   sample-boards})

;; Parse the puzzle input

(defn string->row
  [s]
  (let [split (-> s s/trim (s/split #"\s+"))]
    (mapv read-string split)))

(defn parse-board
  [raw-board]
  (mapv string->row raw-board))

(def init-state
  (let [raw-input (-> "../resources/day04.txt" io/resource io/reader slurp (s/split #"\n"))
        raw-calls (-> (first raw-input) (s/split  #","))
        boards    (->> (drop 2 raw-input)
                       (remove (partial = ""))
                       (partition 5)
                       (mapv parse-board))]
    {:called   '()
     :uncalled (map read-string raw-calls)
     :boards   boards}))

;; Define the terminating condition

(defn cols
  "Reads rows column-wise."
  [board]
  (apply mapv vector board))

(defn has-bingo?
  [called board]
  (let [called-set (set called)]
    ;; All numbers of some row in a given board have been called
    (or (some (fn [row] (every? called-set row))
              board)
        ;; ALl numbers of some col in a given board have been called
        (some (fn [col] (every? called-set col))
              (cols board)))))

;; Implement the gameplay mechanism

(defn score
  ([called board]
   (score called board (first called)))
  ([called board recent-call]
   (let [sum-unmarked (->> board
                           (reduce into [])
                           (remove (set called))
                           (apply +))]
     (* sum-unmarked recent-call))))

(defn play-bingo
  [{called :called uncalled :uncalled boards :boards :as state}]
  (if-some [winner (first (filter (partial has-bingo? called) boards))]
    (score called winner)
    (recur (-> state
               (update :called conj (first uncalled)) ;; conj prepends, not appends, here
               (update :uncalled rest)))))

(defn part-1 []
  (play-bingo init-state))

(defn find-last-winning-board
  [{called :called uncalled :uncalled boards :boards winners :winners
    :or {winners '()}
    :as state}]
  (if (empty? boards) (first winners)
    (let [new-winner (first (filter (partial has-bingo? called) boards))
          remaining  (remove (partial has-bingo? called) boards)]
      (recur (cond-> state
               new-winner (update :winners  conj (score called new-winner (first called)))
               :always    (update :called   conj (first uncalled))
               :always    (update :uncalled rest)
               :always    (assoc  :boards   remaining))))))

(defn part-2 []
  (find-last-winning-board init-state))

(ns aoc2021.day04
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(comment
 "Day 4: Giant Squid")

(def sample-input
  (-> "../resources/day04_ex.txt" io/resource io/reader line-seq))

(def puzzle-input
  (-> "../resources/day04.txt" io/resource io/reader line-seq))

(defn string->row
  [s]
  (let [split (-> s s/trim (s/split #"\s+"))]
    (mapv read-string split)))

(defn init
  [data]
  (let [parse-board (fn [raw-board] (mapv string->row raw-board))
        raw-calls   (-> (first data) (s/split  #","))
        boards      (->> (drop 2 data)
                         (remove empty?)
                         (partition 5)
                         (mapv parse-board))]
    {:called   '() ; These are lists so that "conj" prepends rather than appends
     :winners  '()
     :uncalled (map read-string raw-calls)
     :boards   boards}))

;; Define the terminating condition

(defn cols
  "Reads rows column-wise."
  [board]
  (apply mapv vector board))

(defn has-bingo?
  "Returns true if a board has a row or column bingo."
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
  "Scores a winning bingo board (when used on the turn where it wins)."
  [called board]
  (let [sum-unmarked (->> board
                          (reduce into [])
                          (remove (set called))
                          (apply +))]
    (* sum-unmarked (first called))))

(defn play-bingo
  [{:keys [called uncalled boards winners] :as state} &
   {:keys [last-winner] :or {last-winner false} :as mode}]
  (cond
    (and (not last-winner) (not-empty winners)) (first winners)  ;; Part 1
    (and last-winner (empty? boards))           (first winners)  ;; Part 2
    :else (let [new-winner (first (filter (partial has-bingo? called) boards))
                remaining  (remove (partial has-bingo? called) boards)]
            (recur (cond-> state
                     new-winner (update :winners  conj (score called new-winner))
                     :always    (update :called   conj (first uncalled))
                     :always    (update :uncalled rest)
                     :always    (assoc  :boards   remaining))
                   mode))))

(time
 (let [sample (init sample-input) puzzle (init puzzle-input)]
   (println "Day 4: Giant Squid")
   (println "[Part 1] Sample:" (play-bingo sample) "Puzzle:" (play-bingo puzzle))
   (println "[Part 2] Sample:" (play-bingo sample :last-winner true) "Puzzle:" (play-bingo puzzle :last-winner true))))

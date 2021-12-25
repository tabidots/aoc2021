(ns aoc2021.day21
  (:require [clojure.java.io :as io]))

(comment
 "Day 21: Dirac Dice")

;; TODO: Add docstrings/explanation

(def sample-input
  (-> "../resources/day21_ex.txt" io/resource io/reader line-seq))

(def puzzle-input
  (-> "../resources/day21.txt" io/resource io/reader line-seq))

(defn make-players
  [data]
  (apply merge
    (for [line data
          :let [[player position] (map read-string (re-seq #"\d+" line))]]
      {player {:position position :score 0}})))

(def board [10 1 2 3 4 5 6 7 8 9 10])

(defn advance
  [{:keys [position] :as player} distance]
  (let [destination (->> (mod (+ position distance) 10)
                         (nth board))]
    (-> player
        (assoc :position destination)
        (update :score + destination))))

;; Part 1

(defn init-state
  [data]
  {:die {:times-rolled 0 :current 1} :players (make-players data)})

(defn part-1
  [{:keys [die players] :as state}]
  (let [winner?                     (fn [[_ player]]
                                      (>= (:score player) 1000))
        turns                       [1 1 1 2 2 2]
        {times-rolled :times-rolled
         current      :current}     die]
    (if (some winner? players)
      (-> (remove winner? players) first val :score (* times-rolled))
      (recur
        (let [turn  (->> (mod times-rolled 6) (nth turns))
              rolls (take 3 (iterate inc current))]
          (-> state
              (assoc-in  [:die :current] (-> (last rolls) inc (mod 100)))
              (update-in [:die :times-rolled] + 3)
              (update-in [:players turn] advance (apply + rolls))))))))

;; Part 2

(defn dirac-init-state
  [data]
  {:universes    {(make-players data) 1}
   :turns-taken  0
   :wins         {1 0, 2 0}})

(def roll-freqs
  (frequencies
    (for [a [1 2 3] b [1 2 3] c [1 2 3]]
      (+ a b c))))

(defn take-turn
  [universes current-player]
  (apply merge-with +
    (for [[universe current-count] universes
          [total-roll multiplier]  roll-freqs
          :when (nil? (:winner universe)) ; Don't propagate games with a winner
          :let [{new-pos :position
                 new-score :score} (advance (universe current-player) total-roll)
                winner             (when (>= new-score 21) current-player)]]
      {(-> universe
           (assoc :winner winner)
           (assoc-in [current-player :position] new-pos)
           (assoc-in [current-player :score] new-score))
       (* current-count multiplier)})))

(defn tally-wins
  [wins universes]
  (reduce-kv (fn [r {:keys [winner]} num-universes]
               (if winner (update r winner +' num-universes)
                 r))
             wins universes))

(defn part-2
  [{:keys [universes wins turns-taken] :as state}]
  (if (empty? universes) (val (apply max-key val wins))
    (recur
      (let [current-player ({0 1, 1 2} (mod turns-taken 2))]
        (-> state
            (update :wins tally-wins universes)
            (update :turns-taken inc)
            (update :universes take-turn current-player))))))

(time
 (do
   (println "Day 21: Dirac Dice")
   (println "[Part 1] Sample:" (part-1 (init-state sample-input)) "Puzzle:" (part-1 (init-state puzzle-input)))
   (println "[Part 2] Sample:" (part-2 (dirac-init-state sample-input)) "Puzzle:" (part-2 (dirac-init-state puzzle-input)))))

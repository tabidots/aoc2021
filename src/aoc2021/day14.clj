(ns aoc2021.day14
  (:require [clojure.java.io :as io]))

(comment
 "Day 14: Extended Polymerization")

;; TODO: Add docstrings/explanation

(defn parse
  [data]
  (let [polymer (->> (first data) (partition 2 1) (frequencies))]
    {:polymer (merge-with + polymer {(take-last 1 (first data)) 1})
     :subs    (apply merge
                (for [line (drop 2 data)
                      :let [[a c b] (map first (re-seq #"\w" line))]]
                  {[a c] [[a b] [b c]]}))}))

(def sample-input
  (-> "../resources/day14_ex.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day14.txt" io/resource io/reader line-seq parse))

(defn grow-polymer
  [{:keys [polymer subs] :as state}]
  (assoc state :polymer
    (reduce-kv (fn [res pair count]
                 (if-some [[left right] (get subs pair)]
                   (merge-with + res {left count right count})
                   (assoc res pair count)))
               {} polymer)))

(defn solve
  [data steps]
  (let [end-state (-> (iterate grow-polymer data) (nth steps) :polymer)
        counts    (reduce-kv (fn [res pair count]
                               (merge-with + res {(first pair) count}))
                             {} end-state)]
    (- (apply max (vals counts)) (apply min (vals counts)))))

(time
  (do
    (println "Day 14: Extended Polymerization")
    (println "[Part 1] Sample:" (solve sample-input 10) "Puzzle:" (solve puzzle-input 10))
    (println "[Part 2] Sample:" (solve sample-input 40) "Puzzle:" (solve puzzle-input 40))))

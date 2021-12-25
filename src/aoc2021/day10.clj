(ns aoc2021.day10
  (:require [clojure.java.io :as io]))

(comment
 "Day 10: Syntax Scoring")

(def sample-input
  (-> "../resources/day10_ex.txt" io/resource io/reader line-seq))

(def puzzle-input
  (-> "../resources/day10.txt" io/resource io/reader line-seq))

(def closing-char {"[" "]", "(" ")", "{" "}", "<" ">"})

(defn opening? [a] (get closing-char (str a)))

(defn pair?
  [a' b']
  (let [a (str a') b (str b')]
    (= (closing-char a) b)))

(def checker-scores {")" 3 "]" 57 "}" 1197 ">" 25137})

(defn score-line
  [line]
  (reduce (fn [a b]
            (cond
              (opening? b)        (cons b a)
              (pair? (first a) b) (rest a)
              :else               (reduced (checker-scores (str b)))))
          '() line))

(defn part-1
  [data]
  (->> (map score-line data)
       (filter integer?)
       (reduce +)))

(def autocomplete-scores {")" 1 "]" 2 "}" 3 ">" 4})

(defn complete-line
  [line]
  (map (comp closing-char str) line))

(defn score-autocompletion
  [chars]
  (reduce (fn [res b]
            (+ (* res 5) (autocomplete-scores b)))
          0 chars))

(defn ac-scores
  [data]
  (->> (map score-line data)
       (remove integer?)
       (map (comp score-autocompletion complete-line))
       sort))

(defn part-2
  [data]
  (let [scores (ac-scores data)]
    (nth scores (/ (count scores) 2))))

(time
 (do
   (println "Day 10: Syntax Scoring")
   (println "[Part 1] Sample:" (part-1 sample-input) "Puzzle:" (part-1 puzzle-input))
   (println "[Part 2] Sample:" (part-2 sample-input) "Puzzle:" (part-2 puzzle-input))))

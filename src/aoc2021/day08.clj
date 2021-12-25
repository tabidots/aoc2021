(ns aoc2021.day08
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.set :as set]))

(comment
 "Day 8: Seven Segment Search")

(defn parse
  [data]
  (for [line data]
    (let [[signal-patterns output-values] (s/split line #" \| ")]
      {:signal-patterns (map (comp set s/join sort)
                             (s/split signal-patterns #" "))
       :output-values   (map (comp set s/join sort)
                             (s/split output-values #" "))})))

(def sample-input
  (-> "../resources/day08_ex.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day08.txt" io/resource io/reader line-seq parse))

;; "1" = 2 segments, "4" = 4 segments, "7" = 3 segments, "8" = 7 segments

(defn part-1
  [data]
  (->> (map :output-values data)
       (reduce into [])
       (keep #(get #{2 3 4 7} (count %)))
       (count)))

(defn get-by-segments
  [num-segments patterns]
  (filter #(= (count %) num-segments) patterns))

;; 6 segments = "0,6,9"
;; 5 segments = "2,3,5"

(defn decode-patterns
  [patterns]
  (let [num-one    (first (get-by-segments 2 patterns))
        num-seven  (first (get-by-segments 3 patterns))
        num-four   (first (get-by-segments 4 patterns))
        num-eight  (first (get-by-segments 7 patterns))
        fivers     (get-by-segments 5 patterns)
        sixers     (get-by-segments 6 patterns)
        ;; "9" is the only sixer that is a superset of "4"
        num-nine   (first (filter (partial set/subset? num-four) sixers))
        ;; "0" is the only remaining sixer that is a superset of "1"
        num-zero   (->> (remove #{num-nine} sixers)
                        (filter (partial set/subset? num-one))
                        first)
        num-six    (first (remove #{num-nine num-zero} sixers))
        ;; "3" is the only fiver that is a superset of "1"
        num-three  (first (filter (partial set/subset? num-one) fivers))
        ;; "5" is the only fiver that is a subset of "6"
        num-five   (first (filter #(set/subset? % num-six) fivers))
        num-two    (first (remove #{num-five num-three} fivers))]
    {num-zero "0" num-one "1" num-two "2" num-three "3" num-four "4"
     num-five "5" num-six "6" num-seven "7" num-eight "8" num-nine "9"}))

(defn part-2
  [data]
  (apply +
    (for [line data]
      (let [lut (decode-patterns (:signal-patterns line))]
        (->> (:output-values line)
             (map lut)
             s/join
             (Integer/parseInt))))))

(time
 (do
   (println "Day 8: Seven Segment Search")
   (println "[Part 1] Sample:" (part-1 sample-input) "Puzzle:" (part-1 puzzle-input))
   (println "[Part 2] Sample:" (part-2 sample-input) "Puzzle:" (part-2 puzzle-input))))

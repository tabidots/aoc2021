(ns aoc2021.day01
  (:require [clojure.java.io :as io]))

(def sample-input
  [199 200 208 210 200 207 240 269 260 263])

(def puzzle-input
  (let [raw (-> "../resources/day01.txt" io/resource io/reader line-seq)]
    (map read-string raw)))

(defn part-1 [data]
  (count (filter true? (map > (rest data) data))))

(defn part-2 [data]
  (let [triples (map (partial apply +)
                     (partition 3 1 data))]
    (part-1 triples)))

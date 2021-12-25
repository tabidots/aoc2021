(ns aoc2021.day02
  (:require [clojure.java.io :as io]))

(comment
 "Day 2: Dive!")

(def sample-input
  (-> "../resources/day02_ex.txt" io/resource io/reader line-seq))

(def puzzle-input
  (-> "../resources/day02.txt" io/resource io/reader line-seq))

(defn add-all-units
  [data direction]
  (let [patt (re-pattern (str direction " (\\d+)"))]
    (->> (keep #(second (re-find patt %)) data)
         (map read-string)
         (apply +))))

(defn part-1
  [data]
  (let [[horiz up down] (map (partial add-all-units data) ["forward" "up" "down"])]
    (* horiz (- down up))))

(defn part-2
  [data & {:keys [aim depth horiz] :or {aim 0 depth 0 horiz 0} :as status}]
  (if-not data (* depth horiz)
    (let [[this & those]  data
          [_ direction u] (re-find #"(\w+) (\d)" this)
          unit            (read-string u)]
      (recur those
        (case direction
          "up"      (update status :aim (fnil - 0) unit)
          "down"    (update status :aim (fnil + 0) unit)
          "forward" (-> status
                        (update :horiz (fnil + 0) unit)
                        (update :depth (fnil + 0) (* aim unit))))))))

(time
 (do
   (println "Day 2: Dive!")
   (println "[Part 1] Sample:" (part-1 sample-input) "Puzzle:" (part-1 puzzle-input))
   (println "[Part 2] Sample:" (part-2 sample-input) "Puzzle:" (part-2 puzzle-input))))

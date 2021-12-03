(ns aoc2021.day02
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(def sample-input
  (s/split-lines "forward 5
down 5
forward 8
up 3
down 8
forward 2"))

(def puzzle-input
  (-> "../resources/day02.txt" io/resource io/reader line-seq))

(defn get-units [s]
  (read-string (re-find #"\d" s)))

(defn add-all-units [data direction]
  (->> (filter #(s/starts-with? % direction) data)
       (map get-units)
       (apply +)))

(defn part-1 [data]
  (let [horiz (add-all-units data "forward")
        up    (add-all-units data "up")
        down  (add-all-units data "down")
        depth (- down up)]
    (* horiz depth)))

(defn part-2 [data {aim :aim depth :depth horiz :horiz :as status}]
  (if-not data (* depth horiz)
    (let [[this & those]  data
          [_ direction u] (re-find #"(\w+) (\d)" this)
          unit            (read-string u)]
      (case direction
        "up"      (recur those (update status :aim - unit))
        "down"    (recur those (update status :aim + unit))
        "forward" (recur those (-> status
                                   (update :horiz + unit)
                                   (update :depth + (* aim unit))))))))

(part-2 puzzle-input {:aim 0 :depth 0 :horiz 0})

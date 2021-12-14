(ns aoc2021.day05
  (:require [clojure.java.io :as io]))

(def sample-input
  (-> "../resources/day05_ex.txt" io/resource io/reader line-seq))

(def puzzle-input
  (-> "../resources/day05.txt" io/resource io/reader line-seq))

(defn bidirectional-range
  "Returns an inclusive range between two numbers in the order they are given."
  [a b]
  (if (> a b) (reverse (range b (inc a)))
    (range a (inc b))))

(defn get-points*
  "Enumerates all coordinates represented by the endpoints of a horizontal or vertical line."
  [line & {:keys [diagonal?] :or {diagonal? false}}]
  (let [[_ x1 y1 x2 y2] (map read-string (re-find #"(\d+),(\d+) -> (\d+),(\d+)" line))
        xs (bidirectional-range x1 x2)
        ys (bidirectional-range y1 y2)]
    (cond (= x1 x2) (map #(vector x1 %) ys)
      (= y1 y2) (map #(vector % y1) xs)
      diagonal? (map vector xs ys)
      :else nil)))

(defn find-overlaps
  "Finds all points in the grid that are crossed by more than one line."
  [lines & {:keys [diagonal?] :or {diagonal? false}}]
  (letfn [(get-points [line] (if diagonal? (get-points* line :diagonal? true)
                               (get-points* line)))]
    (->> (reduce into [] (keep get-points lines))
         (frequencies)
         (remove #(= (val %) 1))
         count)))

(defn part-1 [] (find-overlaps puzzle-input))

(defn part-2 [] (find-overlaps puzzle-input :diagonal? true))

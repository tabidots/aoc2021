(ns aoc2021.day13
  (:require [clojure.java.io :as io]))

(defn parse
  [data]
  (apply merge-with conj {:grid [] :folds []}
    (for [line data
          :when (not= line "")]
      (if-some [[_ x y] (re-find #"(\d+),(\d+)" line)]
        {:grid [(read-string x) (read-string y)]}
        (let [[_ axis crease] (re-find #"([yx])=(\d+)" line)]
          {:folds {:axis (keyword axis)
                   :crease (read-string crease)}})))))

(def sample-input
  (-> "../resources/day13_ex.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day13.txt" io/resource io/reader line-seq parse))

(defn fold
  [axis crease grid]
  (set
    (for [[x y] grid]
      (cond
        (and (= axis :y) (> y crease))           ;; y minus twice the difference
        [x (- y (* 2 (Math/abs (- y crease))))]  ;; between y and the crease
        (and (= axis :x) (> x crease))           ;; x minus twice the difference
        [(- x (* 2 (Math/abs (- x crease)))) y]  ;; between x and the crease
        :else [x y]))))

(defn part-1
  [{grid :grid [{:keys [axis crease]} & _] :folds}]
  (count (fold axis crease grid)))

(defn display
  [grid]
  (let [xs (map first grid)
        ys (map second grid)]
    (dotimes [y (inc (apply max ys))]
      (newline)
      (dotimes [x (inc (apply max xs))]
        (if (get grid [x y])
          (print \#)
          (print \ ))))))

(defn part-2
  [data]
  (display
    (reduce (fn [grid {:keys [axis crease]}]
              (fold axis crease grid))
            (:grid data) (:folds data))))

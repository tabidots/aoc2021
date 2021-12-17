(ns aoc2021.day15
  (:require [clojure.java.io :as io]
            [clojure.data.priority-map :refer [priority-map-keyfn]]))

(defn parse
  [data]
  (vec (for [row data]
         (mapv read-string (re-seq #"\d" row)))))

(def sample-input
  (-> "../resources/day15_ex.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day15.txt" io/resource io/reader line-seq parse))

(defn solve
  "Adaptation of Dijkstra's algorithm."
  [m]
  (let [goal         [(dec (count m)) (dec (count (first m)))]
        neighbors-fn (fn [[y x]]
                       (filter #(pos? (get-in m % -1))
                               [[(dec y) x] [(inc y) x] [y (inc x)] [y (dec x)]]))
        lower-cost   (fn [old new] (if (< new old) new old))]
    (loop [stack      (priority-map-keyfn identity [0 0] 0)
           closed-set {}]
      (let [[coords cost :as current] (peek stack)]
        (if (= coords goal) cost
          (let [neighbors (into {}
                                (for [neighbor (neighbors-fn coords)
                                      :when (not (contains? closed-set neighbor))]
                                  {neighbor (+ cost (get-in m neighbor))}))]
            (recur (merge-with lower-cost (pop stack) neighbors)
              (into (conj closed-set current) neighbors))))))))

(defn expand
  [m]
  (letfn [(inc-wrap [x]           (if (>= x 9) 1 (inc x)))
          (expand-across [matrix] (for [row matrix]
                                    (->> row
                                         (iterate (partial map inc-wrap))
                                         (take 5)
                                         (reduce into []))))
          (expand-down [matrix]   (for [row matrix]
                                    (mapv inc-wrap row)))]
    (->> (expand-across m)
         (iterate expand-down)
         (take 5)
         (apply concat)
         vec)))

; (solve puzzle-input)
; (solve (expand puzzle-input))

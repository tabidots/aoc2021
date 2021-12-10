(ns aoc2021.day09
  (:require [clojure.java.io :as io]))

(defn parse
  [data]
  (reduce into {}
    (for [y (range (count data))
          :let [values (map read-string (re-seq #"\d" (nth data y)))]]
      (map-indexed (fn [x value] {[y x] value}) values))))

(defn neighbors
  [[y x]]
  [[(dec y) x] [y (dec x)] [y (inc x)] [(inc y) x]])

(def sample-input
  (-> "../resources/day09_ex.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day09.txt" io/resource io/reader line-seq parse))

(defn low-points
  [heatmap]
  (reduce-kv (fn [m coords this]
               (let [those (keep heatmap (neighbors coords))]
                 (if (every? (partial < this) those)
                   (assoc m coords this)
                   m)))
             {} heatmap))

(defn part-1
  [heatmap]
  (->> (low-points heatmap)
       (map (comp inc val))
       (reduce +)))

(defn basin-size
  [heatmap basin]
  (if-some [good-neighbors (->> (mapcat neighbors basin)
                                (filter #(< (get heatmap % 10) 9))
                                (remove basin)
                                not-empty)]
    (recur heatmap (into basin good-neighbors))
    (count basin)))

(defn part-2
  [heatmap]
  (->> (low-points heatmap)
       (map (comp (partial basin-size heatmap)
                  (fn [[coords _]] #{coords})))
       (sort >)
       (take 3)
       (reduce *)))

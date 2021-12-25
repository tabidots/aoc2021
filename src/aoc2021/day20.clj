(ns aoc2021.day20
  (:require [clojure.java.io :as io]))

(comment
 "Day 20: Trench Map")

(defn parse
  [data]
  (let [lut {\# 1 \. 0}]
    {:algo           (mapv lut (first data))
     :times-enhanced 0
     :image          (->> (drop 2 data)
                          (mapv (partial mapv lut)))}))

(def sample-input
  (-> "../resources/day20_ex.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day20.txt" io/resource io/reader line-seq parse))

(defn square
  [[y x]]
  [[(dec y) (dec x)] [(dec y) x] [(dec y) (inc x)]
   [y (dec x)]       [y x]       [y (inc x)]
   [(inc y) (dec x)] [(inc y) x] [(inc y) (inc x)]])

(defn bin->dec
  [x]
  (Integer/parseInt x 2))

(defn index
  [image [y x] dummy]
  (->> (square [y x])
       (map #(get-in image % dummy))
       (apply str)
       bin->dec))

(defn enhance
  [{:keys [algo image times-enhanced] :as state}]
  ;; Tricky! If the first bit in the algo is 1, then all of the "empty" spaces
  ;; in the infinite region outside the image become 1 upon every other enhancement.
  (let [dummy (if (zero? (first algo)) 0
                (mod times-enhanced 2))]
    (-> state
        (update :times-enhanced inc)
        (assoc :image
          (let [height (count image)
                width  (count (first image))]
            (vec (for [y (range -1 (inc height))]
                   (vec (for [x (range -1 (inc width))]
                          (nth algo (index image [y x] dummy)))))))))))

(defn solve
  [{:keys [times-enhanced image] :as data} iterations]
  (if (= times-enhanced iterations)
    (-> image flatten frequencies (get 1))
    (recur (enhance data) iterations)))

(time
 (do
  (println "Day 20: Trench Map")
  (println "[Part 1] Sample:" (solve sample-input 2) "Puzzle:" (solve puzzle-input 2))
  (println "[Part 2] Sample:" (solve sample-input 50) "Puzzle:" (solve puzzle-input 50))))

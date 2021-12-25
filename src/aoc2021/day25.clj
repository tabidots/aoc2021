(ns aoc2021.day25
  (:require [clojure.java.io :as io]))

(comment
 "Day 25: Sea Cucumber")

(def lut
  {\v :south, \> :east})

(defn parse
  [data]
  (let [grid   (vec (for [line data] (mapv lut line)))
        height (count grid)
        width  (count (first grid))]
    (apply merge-with conj
      {:east #{} :south #{} :width width :height height}
      (for [y (range height)
            x (range width)
            :let [cucumber (get-in grid [y x])]
            :when (some? cucumber)]
        {cucumber [y x]}))))

(def sample-1
  (-> "../resources/day25_ex1.txt" io/resource io/reader line-seq parse))

(def sample-input
  (-> "../resources/day25_ex2.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day25.txt" io/resource io/reader line-seq parse))

(defn east
  [{:keys [south east width] :as state}]
  (let [can-move  (set (remove (fn [[y x]]
                                 (let [neighbor [y (mod (inc x) width)]]
                                    (or (south neighbor) (east neighbor))))
                               east))
        neighbors (set (map (fn [[y x]] [y (mod (inc x) width)]) can-move))
        frozen?   (empty? can-move)]
      (-> state
          (assoc :east-frozen frozen?)
          (update :east (partial remove can-move))
          (update :east into neighbors)
          (update :east set))))

(defn south
  [{:keys [south east height] :as state}]
  (let [can-move  (set (remove (fn [[y x]]
                                 (let [neighbor [(mod (inc y) height) x]]
                                   (or (south neighbor) (east neighbor))))
                               south))
        neighbors (set (map (fn [[y x]] [(mod (inc y) height) x]) can-move))
        frozen?   (empty? can-move)]
    (-> state
        (assoc :south-frozen frozen?)
        (update :south (partial remove can-move))
        (update :south into neighbors)
        (update :south set))))

(def step (comp south east))

(defn draw-grid
  [{:keys [east south width height]}]
  (doseq [y (range height)]
    (println (mapv (fn [x]
                     (cond (east [y x]) ">"
                       (south [y x]) "v"
                       :else "."))
                   (range width)))))

(defn part-1
  [input]
  (->> (iterate step input)
       (take-while #(not (and (:east-frozen %) (:south-frozen %))))
       (count)))

(time
 (do
  (println "Day 25: Sea Cucumber")
  (println "[Part 1] Sample:" (part-1 sample-input) "Puzzle:" (part-1 puzzle-input))))

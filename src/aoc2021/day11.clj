(ns aoc2021.day11
  (:require [clojure.java.io :as io]))

(defn parse
  [data]
  {:flashes 0
   :step 0
   :grid
   (reduce into {}
     (for [y (range (count data))
           :let [values (map read-string (re-seq #"\d" (nth data y)))]]
       (map-indexed (fn [x value] {[y x] value}) values)))})

(defn neighbors
  [[y x]]
  [[(dec y) x] [y (dec x)] [y (inc x)] [(inc y) x]
   [(dec y) (dec x)] [(inc y) (inc x)] [(inc y) (dec x)] [(dec y) (inc x)]])

(def sample-input
  (-> "../resources/day11_ex.txt" io/resource io/reader line-seq parse))

(def puzzle-input
  (-> "../resources/day11.txt" io/resource io/reader line-seq parse))

(defn inc-energy
  [{:keys [grid] :as state}]
  (assoc state :grid
    (reduce-kv (fn [m k v]
                 (assoc m k (inc v)))
               {} grid)))

(defn trigger-flashes
  [{:keys [grid] :as state}]
  (if-some [to-flash (first (filter #(> (val %) 9) grid))]
    (let [coords        (key to-flash)
          new-neighbors (apply merge (for [n (neighbors coords)
                                           :when (grid n)]
                                       {n 1}))]
      (recur
        (-> state
            (update :grid (partial merge-with +) new-neighbors)
            (assoc-in [:grid coords] -1000)
            (update :flashes inc))))
    state))

(defn reset-energy
  [{:keys [grid] :as state}]
  (let [grid' (reduce-kv (fn [m k v]
                           (assoc m k (max 0 v)))
                         {} grid)]
    (-> state
        (assoc :grid grid')
        (update :step inc))))

(def step
  (comp reset-energy trigger-flashes inc-energy))

(defn part-1
  [data]
  (-> (iterate step data)
      (nth 100)
      :flashes))

(defn part-2
  [data]
  (->> (iterate step data)
       (drop-while #(not-every? zero? (vals (:grid %))))
       first
       :step))

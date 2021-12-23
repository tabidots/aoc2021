(ns aoc2021.day22
  (:require [clojure.java.io :as io]))

(defn parse
  [data]
  (for [line data
        :let [action      (keyword (re-find #"on|off" line))
              bounds      (map read-string (re-seq #"-?\d+" line))
              for-part-1? (every? #(<= -50 % 50) bounds)]]
    {:action action :for-part-1? for-part-1? :bounds bounds}))

(def puzzle-input
  (-> "../resources/day22.txt" io/resource io/reader line-seq parse))

(defn abs [x] (Math/abs x))

(defn volume
  "Calculates the volume of a cube given by its x, y, and z bounds."
  [bounds]
  (apply * (map (comp inc abs -) (take-nth 2 (rest bounds))
                                 (take-nth 2 bounds))))

(defn inside?
  "Returns true is the latter cuboid is equal to or smaller than the former."
  [[a1 a2 b1 b2 c1 c2] [x1 x2 y1 y2 z1 z2]]
  (and (<= a1 x1 x2 a2)
       (<= b1 y1 y2 b2)
       (<= c1 z1 z2 c2)))

(defn outside?
  [[a1 a2 b1 b2 c1 c2] [x1 x2 y1 y2 z1 z2]]
  (or (< x2 a1) (> x1 a2)
      (< y2 b1) (> y1 b2)
      (< z2 c1) (> z1 c2)))

(defn difference
  "Compares the latter cuboid to the former and returns a list of vectors
  representing the non-overlapping cuboids on up to 6 sides."
  [[a1 a2 b1 b2 c1 c2 :as cuboid-a] [x1 x2 y1 y2 z1 z2 :as cuboid-b]]
  (when-not (outside? cuboid-a cuboid-b)
    (let [inner-back (max c1 z1)  inner-front (min c2 z2)
          inner-left (max a1 x1)  inner-right (min a2 x2)
          ;inner-floor (max b1 y1) inner-ceiling (min b2 y2)
          right (when (> x2 a2)
                  [(inc a2) x2 y1 y2 z1 z2])
          left (when (< x1 a1)
                 [x1 (dec a1) y1 y2 z1 z2])
          top (when (> y2 b2)
                [inner-left inner-right (inc b2) y2 inner-back inner-front])
          bottom (when (< y1 b1)
                   [inner-left inner-right y1 (dec b1) inner-back inner-front])
          front (when (> z2 c2)
                  [inner-left inner-right y1 y2 (inc c2) z2])
          back (when (< z1 c1)
                 [inner-left inner-right y1 y2 z1 (dec c1)])]
      (remove nil? [right left top bottom front back]))))

(defn expand-cuboid
  [[x1 x2 y1 y2 z1 z2]]
  (for [x (range x1 (inc x2))
        y (range y1 (inc y2))
        z (range z1 (inc z2))]
    [x y z]))

(defn subtract-cuboid
  [source cuboid-to-subtract]
  (when-not (inside? cuboid-to-subtract source)
    (or (not-empty (difference cuboid-to-subtract source))
        [source])))

(defn add-cuboid
  [sources target]
  (if (some #(inside? % target) sources) sources
    (let [subtracted (mapcat #(subtract-cuboid % target) sources)]
      (into #{} (conj subtracted target)))))

(defn reboot
  [instructions]
  (reduce (fn [seen {:keys [bounds action]}]
            ;(println (apply + (map volume seen)))
            (case action
              :on  (add-cuboid seen bounds)
              :off (->> (mapcat #(subtract-cuboid % bounds) seen)
                        (into #{}))))
          #{} instructions))

(defn part-1
  []
  (apply + (map volume (reboot (filter :for-part-1? puzzle-input)))))

(defn part-2
  []
  (apply + (map volume (reboot puzzle-input))))

(ns aoc2021.day17
  (:require [clojure.java.io :as io]))

(defn parse
  [data]
  (let [[left right bottom top] (map read-string (re-seq #"-?\d+" data))]
    {:left left :right right :top top :bottom bottom}))

(def sample-input
  (-> "../resources/day17_ex.txt" io/resource io/reader slurp parse))

(def puzzle-input
  (-> "../resources/day17.txt" io/resource io/reader slurp parse))

(defn step
  [{:keys [y x-velocity y-velocity] :as probe}]
  (-> probe
      (update :x + x-velocity)
      (update :y + y-velocity)
      (update :max-y max y)
      (update :x-velocity (cond
                            (pos? x-velocity)  dec
                            (zero? x-velocity) identity
                            (neg? x-velocity)  inc))
      (update :y-velocity dec)))

(defn hit-target?
  [{:keys [left right top bottom]} {:keys [x y] :as probe}]
  (cond
    (and (<= left x right) (<= bottom y top)) probe
    (< y bottom)                              false
    (or (< x left) (> y top))                 nil
    :else                                     false))

(defn fire-probe
  [target x-velocity y-velocity]
  (when-not (zero? x-velocity)
    (->> {:x 0 :y 0 :x-velocity x-velocity :y-velocity y-velocity :max-y 0}
         (iterate step)
         (keep (partial hit-target? target))
         first)))

;; to hit the target, x-velocity must be positive but
;; cannot be greater than target's right edge
;; y-velocity cannot be less than target's bottom edge, but what's the upper bound?
;; trial and error (and the part 2 example) shows that it's the opposite value
;; of the target's bottom edge

(defn solve
  [{:keys [right bottom] :as target}]
  ((juxt #(:max-y (apply max-key :max-y %))
         count)
   (for [xv (range 1 (inc right))
         yv (range bottom (inc (- bottom)))
         :let [result (fire-probe target xv yv)]
         :when result]
     {:max-y (:max-y result) :init-xv xv :init-yv yv})))

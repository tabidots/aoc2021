(ns aoc2021.day19
  (:require [clojure.java.io :as io]
            [clojure.set :as s]))

(comment
 "Day 19: Beacon Scanner")

(defn rotations
  [[x y z]]
  ;; 0          90º              180º          270º clockwise
  [[x y z], [(- y) x z], [(- x) (- y) z], [y (- x) z]])

(defn orientations-single
  [[x y z]]
  ;; face forward    right          back          left        ceiling       floor
  (->> [[x y z], [(- z) y x], [(- x) y (- z)], [z y (- x)], [x (- z) y], [x z (- y)]]
       (mapcat rotations)))

(defn orientations
  "Given a seq of beacon coordinates, returns 24 seqs corresponding to the coordinates
  of the same beacons under a different orientation + rotation of the scanner."
  [beacons]
  (->> (map orientations-single beacons) ;; each row is a beacon, each column is an orientation
       (apply mapv vector)))             ;; each row is an orientation, each column is a beacon

(defn parse
  [path]
  (with-open [rdr (io/reader (io/resource path))]
    (loop [[line & lines] (line-seq rdr)
           scanners       {}
           current-id     nil
           beacons        []]
      (cond
        (nil? line)   (assoc scanners current-id {:beacons beacons})
        (empty? line) (recur lines (assoc scanners current-id {:beacons beacons}) nil nil)
        :else
        (let [[_ scanner-id] (map read-string (re-find #"scanner (\d+)" line))
              coords         (mapv read-string (re-seq #"-?\d+" line))]
          (if scanner-id
            (recur lines scanners scanner-id [])
            (recur lines scanners current-id (conj beacons coords))))))))

(def sample-input
  (-> "../resources/day19_ex.txt" parse))

(def puzzle-input
  (-> "../resources/day19.txt" parse))

(defn translate
  [dx dy dz beacons]
  (map (fn [[x y z]] [(+ x dx) (+ y dy) (+ z dz)]) beacons))

(defn reorient
  "Finds the position of the target scanner relative to a reference scanner.
  and returns the position and the scanner's beacons translated to that position.
  Returns nil if the two scanners do not share any beacons."
  [reference target]
  (first
    (for [reference-beacon  (:beacons reference)
          candidate-beacons (orientations (:beacons target))
          candidate-beacon  candidate-beacons
          :let [[dx dy dz]  (map - reference-beacon candidate-beacon)
                translated  (translate dx dy dz candidate-beacons)
                overlaps    (s/intersection (set (:beacons reference))
                                            (set translated))]
          :when (>= (count overlaps) 12)]
      {:position [dx dy dz]
       :beacons  translated})))

(defn filter-keys
  "Given a map, returns a vector of all keys whose values satisfy pred."
  [pred m]
  (reduce-kv (fn [r k v]
               (if (pred v) (conj r k)
                 r))
             [] m))

(defn remove-keys
  [pred m]
  (filter-keys (complement pred) m))

(declare part-1)
(declare part-2)

(defn solve
  [scanners]
  (let [[knowns unknowns] ((juxt filter-keys remove-keys) :position scanners)]
    ;(println knowns unknowns)
    (cond
      (empty? knowns)   (recur (assoc-in scanners [0 :position] [0 0 0]))
      (empty? unknowns) ((juxt part-1 part-2) scanners)
      :else
      (recur (apply merge scanners
               (for [k knowns
                     u unknowns
                     :when (not (:used (scanners k)))
                     :let [new-scanner (reorient (scanners k) (scanners u))]
                     :when new-scanner]
                 {k (assoc (scanners k) :used true)
                  u new-scanner}))))))

(defn part-1
  [scanners]
  (count
    (reduce-kv (fn [r _ {beacons :beacons}]
                 (into r beacons))
               #{} scanners)))

(defn manhattan-distance
  [a b]
  (apply + (map (fn [x y]
                  (Math/abs (- x y)))
                a b)))

(defn part-2
  [scanners]
  (apply max
    (for [[a {p1 :position}] scanners
          [_ {p2 :position}] (dissoc scanners a)]
      (manhattan-distance p1 p2))))

(println "Day 19 solution takes too long. Skipping...")

(comment "This takes a few minutes."
 (let [[p1s p2s] (solve sample-input)
       [p1p p2p] (solve puzzle-input)]
   (println "Day 19: Beacon Scanners")
   (println "[Part 1] Sample:" p1s "Puzzle:" p1p)
   (println "[Part 2] Sample:" p2s "Puzzle:" p2p)))

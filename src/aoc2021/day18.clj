(ns aoc2021.day18
  (:require [clojure.java.io :as io]
            [clojure.zip :as z]
            [clojure.walk :as w]
            [clojure.math.combinatorics :as combo]))

(comment
 "Day 18: Snailfish")

;; This could probably be cleaned up, but I don't want to touch this problem
;; ever again. Ugh.

(def example-1 (-> "../resources/day18_ex.txt" io/resource io/reader line-seq))
(def example-2 (-> "../resources/day18_ex2.txt" io/resource io/reader line-seq))
(def puzzle-input (-> "../resources/day18.txt" io/resource io/reader line-seq))

(defn zip-return
  [tree]
  (-> tree z/root z/vector-zip))

(defn first-in-tree
  [pred tree]
  (loop [loc (z/next tree)]
    (cond
      (pred loc)   loc
      (z/end? loc) nil
      :else        (recur (z/next loc)))))

(defn pair? [x]
  (and (vector? x) (integer? (first x)) (integer? (second x))))

(def to-explode
  #(and (>= (count (z/path %)) 4)
        (pair? (z/node %))))

(def to-split
  #(and (integer? (z/node %))
        (>= (z/node %) 10)))

(defn explode-left
  [tree]
  (if-some [explodee (first-in-tree to-explode tree)]
    (loop [loc (-> explodee z/prev)]
      (cond
        (integer? (z/node loc)) (zip-return (z/edit loc + (first (z/node explodee))))
        (nil? (z/up loc))       tree ;; found no neighbor to edit
        :else                   (recur (z/prev loc))))
    tree))

(defn explode-right
  [tree]
  (if-some [explodee (first-in-tree to-explode tree)]
    (loop [loc (-> explodee z/next z/next z/next)]
      (cond
        (integer? (z/node loc)) (zip-return (z/edit loc + (second (z/node explodee))))
        (z/end? loc)            tree ;; found no neighbor to edit
        :else                   (recur (z/next loc))))
    tree))

(defn explode-replace
  [tree]
  (if-some [explodee (first-in-tree to-explode tree)]
    (zip-return (z/replace explodee 0))
    tree))

(defn snail-reduce*
  [tree]
  (if (first-in-tree to-explode tree)
    (-> tree explode-left explode-right explode-replace)
    (when-some [split (first-in-tree to-split tree)]
      (let [half (/ (z/node split) 2)
            left (int (Math/floor half))
            right (int (Math/ceil half))]
        (zip-return (z/replace split [left right]))))))

(defn snail-reduce
  [tree]
  (->> (iterate snail-reduce* tree)
       (take-while some?)
       last))

(defn snail-add
  [a b]
  (let [a' (if (and (vector? a) (nil? (peek a)))  ;; remove the nil at the tail end
             (str (first a))
             (str a))
        b' (str b)]
    (-> (str "[" a' "," b' "]")
        read-string
        z/vector-zip
        snail-reduce)))

(defn magnitude
  [x]
  (if (and (vector? x) (= (count x) 2))
    (+ (* 3 (magnitude (first x)))
       (* 2 (magnitude (second x))))
    x))

(defn part-1
  [homework]
  (->> (reduce snail-add homework)
       first ;; remove the nil at the tail end
       (w/postwalk magnitude)))

(defn part-2
  [homework]
  (apply max (map part-1 (combo/permuted-combinations homework 2))))

(time
 (let [examples [example-1 example-2]]
   (println "Day 18: Snailfish")
   (println "[Part 1] Examples:" (map part-1 examples) "Puzzle:" (part-1 puzzle-input))
   (println "[Part 2] Examples:" (map part-2 examples) "Puzzle:" (part-2 puzzle-input))))

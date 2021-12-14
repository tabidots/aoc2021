(ns aoc2021.day12
  (:require [clojure.java.io :as io]))

(defn parse
  [data]
  (reduce (fn [res line]
            (let [[head tail] (map keyword (re-seq #"\w+" line))]
              (cond-> res
                (and (not (= head :end)) (not (= tail :start)))
                (update head (fnil conj []) tail)
                (and (not (= tail :end)) (not (= head :start)))
                (update tail (fnil conj []) head))))
          {} data))

(def sample-1 (-> "../resources/day12_ex1.txt" io/resource io/reader line-seq parse))
(def sample-2 (-> "../resources/day12_ex2.txt" io/resource io/reader line-seq parse))
(def sample-3 (-> "../resources/day12_ex3.txt" io/resource io/reader line-seq parse))
(def puzzle-input (-> "../resources/day12.txt" io/resource io/reader line-seq parse))

(defn small-cave? [c] (re-find #"^[a-z]{1,2}$" (name c)))

(defn make-path
  [system path & {:keys [single-twice?] :or {single-twice? false}}]
  (if (contains? (set path) :end) [path]
    (let [visited-small-caves  (filter small-cave? path)
          small-visited-twice? (not (or (empty? visited-small-caves)
                                        (apply distinct? visited-small-caves)))
          choices
          (cond->> (get system (last path))
             (not single-twice?)        (remove (set visited-small-caves))
             (and single-twice?
                  small-visited-twice?) (remove (set visited-small-caves)))]

      (map (partial conj path) choices))))

(defn take-while-distinct
  [coll]
  ;; https://github.com/taylorwood/advent-of-code/blob/master/src/advent_of_code/2017/16.clj
  (let [seen (volatile! #{})]
    (take-while (fn [v]
                  (if (@seen v) false
                    (vswap! seen conj v)))
                coll)))

(defn solve
  [system & {:keys [single-twice?] :or {single-twice? false}}]
  (->> [[:start]]
       (iterate (fn [paths]
                  (mapcat #(make-path system % :single-twice? single-twice?)
                          paths)))
       take-while-distinct
       last
       (filter #(= (last %) :end))
       count))

(solve puzzle-input)

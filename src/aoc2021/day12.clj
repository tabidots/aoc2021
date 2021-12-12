(ns aoc2021.day12
  (:require [clojure.java.io :as io]))

(defn parse
  [data]
  (reduce (fn [res line]
            (let [[_ head tail] (map keyword (re-find #"(\w+)\-(\w+)" line))]
              (-> res
                (update head (fnil conj []) tail)
                (update tail (fnil conj []) head))))
          {} data))

(defn trim
  [data]
  (reduce-kv (fn [res node neighbors]
               (cond
                 (get (set neighbors) "start") (assoc res node (remove #{"start"} neighbors))
                 (= node "end") res
                 :else (assoc res node neighbors)))
             {} data))

(def sample-1 (-> "../resources/day12_ex1.txt" io/resource io/reader line-seq parse trim))
(def sample-2 (-> "../resources/day12_ex2.txt" io/resource io/reader line-seq parse trim))
(def sample-3 (-> "../resources/day12_ex3.txt" io/resource io/reader line-seq parse trim))
(def puzzle-input (-> "../resources/day12.txt" io/resource io/reader line-seq parse trim))

(defn small-cave? [c] (re-find #"^[a-z]{1,2}$" (name c)))

(defn make-path
  [system path & {:keys [single-twice?] :or {single-twice? false}}]
  (if (contains? (set path) :end) [path]
    (let [visited-small-caves  (filter small-cave? path)
          small-visited-twice? (not (or (empty? visited-small-caves)
                                        (apply distinct? visited-small-caves)))
          choices
          (cond->> (get system (last path))
               :always                    (remove #{:start})
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

(defn part-1
  [system]
  (->> [[:start]]
       (iterate (fn [paths]
                  (mapcat #(make-path system %)
                          paths)))
       take-while-distinct
       last
       (filter #(= (last %) :end))
       count))

(defn part-2
  [system]
  (->> [[:start]]
       (iterate (fn [paths]
                  (mapcat #(make-path system % :single-twice? true)
                          paths)))
       take-while-distinct
       last
       (filter #(= (last %) :end))
       count))

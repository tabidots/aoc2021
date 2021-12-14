(ns aoc2021.day03
  (:require [clojure.java.io :as io]))
  
(def sample-input
  (-> "../resources/day03_ex.txt" io/resource io/reader line-seq))

(def puzzle-input
  (-> "../resources/day03.txt" io/resource io/reader line-seq))

(defn cols
  "Reads rows column-wise."
  [rows]
  (apply mapv vector rows))

(defn gamma-bit
  "Finds the most common bit in a column, or returns nil if there is an
  equal number of 1s and 0s. Returns Character (\1 or \0)."
  [column]
  (let [freqs (frequencies column)]
    (when (apply distinct? (vals freqs))
      (key (apply max-key val freqs)))))

(defn char->digit
  [c]
  (Character/digit c 10))

(defn invert
  "Inverts a bit."
  [n]
  (inc (- n)))

(defn binseq->decimal
  "Converts a seq of bits to decimal."
  [binseq]
  (Integer/parseInt (apply str binseq) 2))

(defn part-1
  "Power consumption = Gamma rate * epsilon rate."
  [data]
  (let [gamma-bits   (map (comp char->digit gamma-bit) (cols data))
        gamma-rate   (binseq->decimal gamma-bits)
        epsilon-rate (binseq->decimal (map invert gamma-bits))]
    (* gamma-rate epsilon-rate)))

(defn oxygen-rating
  [data]
  (loop [candidates data
         index      0]
    (if-not (next candidates)
      (binseq->decimal (first candidates))
      (let [cur-col   (nth (cols candidates) index)

            cur-gamma (or (gamma-bit cur-col) \1)]
        (recur
          (filter #(= cur-gamma (nth % index)) candidates)
          (inc index))))))

(defn co2-rating
  [data]
  (loop [candidates data
         index      0]
    (if-not (next candidates)
      (binseq->decimal (first candidates))
      (let [cur-col   (nth (cols candidates) index)
            cur-gamma (gamma-bit cur-col)
            cur-eps   (case cur-gamma
                        \1  \0
                        \0  \1
                        nil \0)]
        (recur
          (filter #(= cur-eps (nth % index)) candidates)
          (inc index))))))

(defn part-2
  "Life support = Oxygen rating * CO2 scrubber rating"
  [data]
  (* (oxygen-rating data) (co2-rating data)))

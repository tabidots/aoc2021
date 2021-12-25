(ns aoc2021.core)

(defn run-all
  []
  (println "Solutions to Advent of Code 2021")
  (println "by Justin Douglas")
  (newline)
  (letfn [(pad-zero [x] (if (< x 10) "0" ""))]
    (doseq [day (range 1 26)]
      (load (str "day" (pad-zero day) (str day)))
      (newline))))

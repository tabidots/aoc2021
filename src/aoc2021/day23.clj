(ns aoc2021.day23
  (:require [clojure.java.io :as io]
            [clojure.data.priority-map :refer [priority-map]]))

(comment
 "Day 23: Amphipod")

;; TODO: Refactor maybe?

(def cost       {:A 1 :B 10 :C 100 :D 1000})
(def goal-rooms {:A 2 :B 4 :C 6 :D 8})
;; nil values denote where amphipods cannot land
(def hallway    [:h :h nil :h nil :h nil :h nil :h :h])
(def rooms      [nil nil :A nil :B nil :C nil :D nil nil])
(def board      (atom [hallway rooms rooms]))

(defn parse
  [data]
  (let [amphipod-names (map keyword (re-seq #"[A-Z]" data))]
    (zipmap (range (count amphipod-names))
            (map (fn [a-type y x]
                   (hash-map :a-type a-type :energy-used 0
                             :position [y x] :history #{}))
                 amphipod-names                         ; -> a-type
                 (mapcat #(repeat 4 %) (iterate inc 1)) ; -> y
                 (cycle [2 4 6 8])))))                  ; -> x

(def sample-input
  (-> "../resources/day23_ex.txt" io/resource io/reader slurp parse))

(def puzzle-input
  (-> "../resources/day23.txt" io/resource io/reader slurp parse))

(def sample-2
  (-> "../resources/day23_p2_ex.txt" io/resource io/reader slurp parse))

(def puzzle-2
  (-> "../resources/day23_p2.txt" io/resource io/reader slurp parse))

(defn vacant?
  "Returns true if the given board position is empty."
  [coords amphipods]
  (not-any? (fn [[_ {:keys [position]}]]
              (= position coords))
            amphipods))

(defn home?
  "Given an amphipod, returns true if the given amphipod is in a compatible room."
  [{:keys [a-type position]}]
  (= a-type (get-in @board position)))

(defn blocked?
  [{:keys [position]} amphipods]
  (let [[y x] position]
    (and (pos? y)
         (not (vacant? [(dec y) x] amphipods)))))

(defn goal?
  "Given a map of amphipods, returns true if all amphipods are in a compatible room."
  [amphipods]
  (every? home? (vals amphipods)))

(defn total-cost
  [amphipods]
  (reduce + (map :energy-used (vals amphipods))))

(defn path
  [origin [y2 x2 :as destination]]
  (when destination
    (loop [[y x :as current] origin
           path []]
      (cond
        ;; If we've made it, then return the path without the origin
        (= current destination)     (rest (conj path current))
        ;; Still in a room, but not at destination x-position -> Move up
        (and (pos? y) (not= x x2))  (recur [(dec y) x] (conj path current))
        ;; Above destination x-position -> Move down
        (= x x2) (recur [(inc y) x] (conj path current))
        ;; In hallway to right of destination -> Move left to destination x-position
        (< x2 x) (recur [y x2] (into path (map #(vector 0 %) (reverse (range (inc x2) (inc x))))))
        ;; In hallway to left of destination -> Move right to destination x-position
        :else    (recur [y x2] (into path (map #(vector 0 %) (range x x2))))))))

(defn possible-destinations
  [{:keys [a-type position history] :as amphipod} amphipods]
  (let [my-goal-rooms         (->> (range 1 (count @board))
                                   (map #(vector % (goal-rooms a-type)))
                                   set)
        goal-room-inhabitants (->> amphipods
                                   (filter #(my-goal-rooms (:position (val %))))
                                   (map #(:a-type (val %)))
                                   set)
        goals-safe?           (or (= goal-room-inhabitants #{a-type})
                                  (empty? goal-room-inhabitants))]
    ;; If amphipod is fully home, there are no possible moves
    (if (and (home? amphipod) goals-safe?) []
      ;; An amphipod can only go into the deepest vacant goal room
      (let [deepest-goal-room (when goals-safe?
                                (->> my-goal-rooms
                                     (filter #(vacant? % amphipods))
                                     (sort-by first >)
                                     (first)))
            path-to-goal      (path position deepest-goal-room)]
        ;; If the goal is open and the path there is too, that's the only possible move
        (if (and path-to-goal
                 (every? #(vacant? % amphipods) path-to-goal))
          [[deepest-goal-room (* (cost a-type) (count path-to-goal))]]
          ;; If amphipod is in the hallway and the goal is not open, there are no possible moves
          (if (zero? (first position)) []
            ;; If amphipod is in a room but not fully home, all hallway spaces are possible
            (let [vacant-hallways (->> (first @board)
                                       (keep-indexed (fn [i x] (when x [0 i])))
                                       (filter #(vacant? % amphipods))
                                       not-empty)]
              (for [coords vacant-hallways
                    :when (not (history coords))
                    :let [my-path (path position coords)]
                    :when (every? #(vacant? % amphipods) my-path)
                    :let [my-cost (* (cost a-type) (count my-path))]]
                 [coords my-cost]))))))))

(comment
  "No longer using this, but it's good for debugging states"
  (defn possible-moves
    [amphipods]
    (for [[id amphipod] amphipods
          :when (not (blocked? amphipod amphipods))
          [destination my-cost] (possible-destinations amphipod amphipods)]
      {id (-> amphipod
              (assoc  :position    destination)
              (update :history     conj destination)
              (update :energy-used + my-cost))})))

(defn next-states
  [amphipods]
  (sort-by total-cost <
    (for [[id amphipod] amphipods
          :when (not (blocked? amphipod amphipods))
          [destination my-cost] (possible-destinations amphipod amphipods)]
      (-> amphipods
          (assoc-in  [id :position] destination)
          (update-in [id :history] conj destination)
          (update-in [id :energy-used] + my-cost)))))

(defn solve
  "Adaptation of Dijkstra's algorithm. Very slow for this problem."
  [init-state]
  (let [lower-cost (fn [old new] (if (< new old) new old))]
    (loop [stack      (priority-map init-state 0)
           closed-set {}]
      (let [[state cur-cost :as current] (peek stack)]
        (if (goal? state) cur-cost
          (let [neighbors (into {}
                                (for [neighbor (next-states state)
                                      :when (not (contains? closed-set neighbor))]
                                  {neighbor (total-cost neighbor)}))]
            (recur (merge-with lower-cost (pop stack) neighbors)
              (into (conj closed-set current) neighbors))))))))

(defn part-1
  []
  (reset! board [hallway rooms rooms])
  (solve puzzle-input))

(defn part-2
  []
  (reset! board [hallway rooms rooms rooms rooms])
  (solve puzzle-2))

(println "Day 23 solution takes too long. Skipping...")

;; Current runtime for each: A few minutes

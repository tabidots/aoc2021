(ns aoc2021.day16
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.pprint :as pp]))

(def hex->bin*
  {\0 "0000" \1 "0001" \2 "0010" \3 "0011" \4 "0100" \5 "0101" \6 "0110"
   \7 "0111" \8 "1000" \9 "1001" \A "1010" \B "1011" \C "1100" \D "1101"
   \E "1110" \F "1111"})

(defn hex->bin
  [hex]
  (vec (mapcat hex->bin* hex)))

(def examples
  (map hex->bin
       ["D2FE28"
        "38006F45291200"
        "EE00D40C823060"
        "8A004A801A8002F478"
        "620080001611562C8802118E34"
        "C0015000016115A2E0802F182340"
        "A0016C880162017C3686B18A3D4780"
        "C200B40A82"
        "04005AC33890"
        "880086C3E88112"
        "CE00C43D881120"
        "D8005AC2A8F0"
        "F600BC2D8F"
        "9C005AC2F8F0"
        "9C0141080250320F1802104A08"]))

(def puzzle-input
  (-> "../resources/day16.txt" io/resource io/reader slurp s/trim hex->bin))

(def vec->int
  (comp #(BigInteger. % 2) s/join))

(defn evaluate
  [op packets]
  (or (get {0 (apply + packets) 1 (apply * packets)
            2 (apply min packets) 3 (apply max packets)} op)
      (get {5 (if (> (first packets) (second packets)) 1 0)
            6 (if (< (first packets) (second packets)) 1 0)
            7 (if (= (first packets) (second packets)) 1 0)} op)))

(defn make-payload
  [data start]
  ;; Subvec throws an exception if the indexes are out of range, so this is
  ;; kind of a hacky way to pretend that it fails silently
  (try
    (let [packet-type (vec->int (subvec data (+ start 3) (+ start 6)))
          length-type (nth data (+ start 6))
          template    {:version     (vec->int (subvec data start (+ start 3)))
                       :packet-type packet-type}]
      (cond-> template
        (and (not= packet-type 4) (= length-type \1))
        , (assoc :target-packets (vec->int (subvec data (+ start 7) (+ start 18))))
        (and (not= packet-type 4) (= length-type \0))
        , (assoc :target-bits (vec->int (subvec data (+ start 7) (+ start 22))))))
    (catch Exception _ nil)))

(declare literal-value)

(defn get-packets
  ([data]
   (get-packets {} data))
  ([packets data & {:keys [target-packets] :or {target-packets nil}}]
   (loop [start   0
          result  packets
          current (make-payload data start)]
     (cond
       ;; Reached end of the stream or accumulated sufficient number of packets
       ;; -> Return result, but if this is the outermost packet, no need to wrap it
       (or (= (count result) target-packets) (nil? current))
       (if (map? result) result {:res result :sub-end start})

       ;; Packet type is 4 -> Extract literal value
       (= (:packet-type current) 4)
       (let [{:keys [value new-start]} (literal-value data (+ start 6))]
         (recur new-start
           (conj result (assoc current :value value))
           (make-payload data new-start)))

       :else ;; Find sub-packets either by packet count or bit length
       (let [sub-start  (if (:target-packets current)
                          (+ start 18)
                          (+ start 22))

             {:keys [res sub-end]}
             (if (:target-packets current)
               (get-packets [] (subvec data sub-start)
                            :target-packets (:target-packets current))
               (get-packets []
                            (subvec data sub-start (+ sub-start (:target-bits current)))))

             new-start   (+ sub-start sub-end)
             value       (evaluate (:packet-type current) (map :value res))]

         (recur new-start
           (conj result (assoc current :packets res :value value))
           (make-payload data new-start)))))))

(defn literal-value
  [data start]
  (loop [end (+ start 5)]
    (let [slice     (subvec data start end)
          indicator (nth slice (- (count slice) 5))]
      (if (= \0 indicator)
        {:value     (->> (partition 5 slice) (mapcat rest) vec->int)
         :new-start end}
        (recur (+ end 5))))))

(defn part-1
  [data]
  (->> (get-packets data)
       (tree-seq map? :packets)
       (map :version)
       (reduce +)))

(defn part-2
  [data]
  (:value (get-packets data)))

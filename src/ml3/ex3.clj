(ns ml3.ex3
  (:use (incanter core)
        (ml util logistic gd matlab)))

(def d (read-dataset-mat5 "src/ml3/ex3data1.mat"))

(defn one-vs-all [X y num-labels lambda iters]
  (let [m (nrow X) X (add-intercept X) initial-theta (zeroes (ncol X))]
    (reduce (fn [all-theta c]
              (let [samples (into [] (map #(if (= % c) 1 0) y))
                    theta (gradient-descent logistic-hypothesis X samples initial-theta :num-iters iters :lambda lambda)]
                (println c (logistic-cost X samples theta))
                (conj all-theta theta)))
      [] (range 1 (inc num-labels)))))

(def y (map int (d :y )))
(def all-theta (matrix (one-vs-all (d :X ) y 10 0.1 1000)))

; X is 5000x400, all-theta is 10x401, s is 5000x10
(def s (mmult (add-intercept (d :X )) (trans all-theta)))

(defn max-index [coll]
  (inc (first (reduce #(if (> (second %1) (second %2)) %1 %2) (map-indexed vector coll)))))

(println "accuracy" (double (accuracy (map max-index (to-vect s)) y)))

; iters   accy
;  5       65%
;  50     75%
;  200   80%
;  500   82%
; 1000  84%
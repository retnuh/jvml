(ns ml.util
  (:gen-class)
  (:import (incanter Matrix))
  (:use (incanter core stats)))

(defn ^Matrix add-intercept [^Matrix X] (bind-columns (repeat (nrow X) 1) X))

(def std (comp sqrt variance))

(defn feature-normalize [^Matrix X]
  (let [m (nrow X) xt (trans X) mu (map mean xt) sigma (map std xt)]
    { :data (div (minus X (conj-rows (repeat m mu))) (repeat m sigma)) :mean mu :sigma sigma}))

(defn- repeats [n x] (into [] (repeat n x)))

(defn zeroes [^long n] (repeats n 0))

(defn ones [^long n] (repeats n 1))
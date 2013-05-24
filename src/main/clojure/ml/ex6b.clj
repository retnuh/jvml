(ns ml.ex6b
  (:use (incanter core charts)
        (ml matlab svm)))

(defn eval-gaussian-kernel [x1 x2 sigma]
  (.evaluate (gaussian-kernel sigma) (sparse-vector x1) (sparse-vector x2)))

(if *command-line-args*
  (let [{:keys [X y]} (read-dataset-mat5 "data/ex6data2.mat")
        C 1
        vectors (model-vectors (train-model X y C (gaussian-kernel 0.1)))]
    (doto
      (scatter-plot (sel X :cols 0) (sel X :cols 1) :x-label "" :y-label "" :title (str "Gaussian Kernel, C=" C) :group-by y)
      (add-points (map first vectors) (map second vectors))
      (view))))

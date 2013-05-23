(ns ml.kmeans
  (:use (ml util)
        (incanter core stats)))

(defn- find-closest-centroid [point centroids]
  (indexes-of? < (map #(sum-of-squares (minus point %)) centroids)))

(defn find-closest-centroids [X centroids]
  (map #(find-closest-centroid % centroids) (to-list X)))

(defn- update-sums [[sums counts] [point idx]]
  [(assoc sums idx (doall (plus point (sums idx)))) (assoc counts idx (inc (counts idx)))])

(defn compute-centroids [X idx k]
  (let [ic (into [] (repeat k 0))
        is (into [] (repeat k (into [] (repeat (ncol X) 0))))
        [s c] (reduce update-sums [is ic] (map vector (to-list X) idx))]
    (matrix (map div s c))))

(defn- kmeans [X centroids]
  (let [k (nrow centroids)
        idx (find-closest-centroids X centroids)]
    (compute-centroids X idx k)))

(defn run-kmeans [X initial-centroids n]
  (first (drop n (iterate #(kmeans X %) initial-centroids))))

(defn init-centroids [X k]
  (sel X :rows (take k (permute (range (nrow X))))))
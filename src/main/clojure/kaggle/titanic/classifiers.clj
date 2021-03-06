(ns kaggle.titanic.classifiers)

;
; mapping strings to integers and flagging missing fields (fare, age and embarked)
;
(defn- sex [pass] (assoc pass :sex (if (= (:sex pass) "male") 1 0)))

(defn- embarked [{e :embarked :as pass}]
  (let [embarked? (if (= "" e) 0 1)]
    (assoc pass :embarked? embarked? :embarked (cond (= e "C") 1 (= e "S") 2 (= e "Q") 3 :else 0))))

(defn- fare [{f :fare s :sibsp p :parch :as pass}]
  (let [fare (if (= "" f) 0 f)
        fare? (if (zero? fare) 0 1)
        family (inc (+ s p))]
    (assoc pass :fare? fare? :fare (/ fare family) :family family)))

(defn- age [{a :age :as pass}]
  (let [age (if (= "" a) 0 a)
        age? (if (zero? age) 0 1)]
    (assoc pass :age? age? :age age)))

(defn- cabin [{c :cabin :as pass}]
  (assoc pass :cabin? (if (= "" c) 0 1)))

; "Beckwith, Mrs. Richard Leonard (Sallie Monypeny)"
(defn- name-parts [name]
  (let [munge-re #"([^,]+),\s+([^.]+)\.\s+(\S+)\s*(?:.*?)?(?:\s*\(.*\s+([^)]+)\))?"
        parts (rest (re-matches munge-re name))]
    (zipmap [:last :title :first :unmarried ] parts)))

(defn- names [pass]
  (merge pass (name-parts (:name pass))))

; if title is Miss, split by age into children and "other"
; treat other titles as Mr/Mrs/Master/Miss based on age
(defn- title [{:keys [title sex age family] :as pass}]
  (let [child? (< age 13)
        female-child? (if (zero? age) (> family 1) child?)]
    (assoc pass :title (cond
                         (= title "Mr") 1
                         (= title "Mrs") 2
                         (= title "Master") 3
                         (= title "Miss") (if female-child? 4 5)
                         :else (if (= sex 1) (if child? 3 1) (if female-child? 4 2))))))

(defn cleanup-classifiers [passengers]
  (map (comp embarked cabin title fare age sex names) passengers))

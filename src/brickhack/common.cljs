(ns brickhack.common
  (:require [quil.core :as q]))

(defn coords-with-radian
  [x y r rScalar]
  [(+ x (* rScalar (Math/cos r)))
   (+ y (* rScalar (Math/sin r)))])

(defn add-with-rollover
  "Calculates the next position based on the current, the speed and a max."
  [current delta max]
  (mod (+ current delta) max))

(defn average
  "Used for updating velocities and sizes"
  [current new]
  (/ (+ current new) 2))

(defn round-to 
  [value step]
  (* (Math/round (/ value step)) step))

(def palettes
  [{:name       "white on blue"
   :background [228 91 20]
   :colors     [[198 47 83]
                [198 25 86]
                [184 22 99]
                [190 10 90]
                [1   91 80]
                [190 0  93]
                [184 32 52]
                [185 23 60]
                [196 3  60]]}
   {:name       "salmon and blue"
   :background [58 5 100]
   :colors     [[354 74  70]
                [354 64 100]
                [200 74  80]
                [200 69  70]
                [1   78  75]
                [58  20 100]
                [1   64 100]
                [205 100 80]
                [205 50  80]]}
   {:name       "sepia and blue"
   :background [221 7 98]
   :colors     [[50  89  75]
                [50  33 100]
                [50  43  95]
                [242 100 69]
                [242 43  99]
                [232 70  80]
                [232 40  90]]}
   {:name       "pale green and brick"
   :background [139 7 100]
   :colors     [[139 35 70]
                [139 15 90]
                [139 25 100]
                [1 55 70]
                [1 85 70]
                [1 25 100]]}])

(defn particle
  "Create a particle obj"
  [id width height palette]
  {:id        id
   :vx        0
   :vy        0
   :size      2
   :direction 0
   :length    0
   :x         (q/random width)
   :y         (q/random height)
   :color     (rand-nth (:colors palette))})
(defn particle-trail
  "Create a particle obj"
  [id x y palette]
  {:id        id
   :size      2
   :x         '(x)
   :y         '(y)
   :color     (rand-nth (:colors palette))})

(defn noise-field 
  "Create a perlin noise field with a certain resolution"
  ([x y zoom] (noise-field x y zoom 0))
  ([x y zoom confounding]
    (q/noise (* x zoom)
             (* y zoom)
             confounding)))

(defn normalize-to
  "Rescale a 0-1 value to min-max range"
  ([value max]
   (normalize-to value 0 max))
  ([value min max]
   (+ min (* value (- max min)))))
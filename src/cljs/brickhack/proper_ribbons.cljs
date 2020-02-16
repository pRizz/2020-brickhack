(ns brickhack.proper-ribbons
  (:require [brickhack.common :as c]
            [quil.core :as q]
            [quil.middleware :as middleware]))

(def window-width (.-innerWidth js/window))
(def window-height (.-innerHeight js/window))

; This-sketch custom code
(def palette (rand-nth c/palettes))

(defn trail
  [id]
  (c/particle-trail id (q/random window-width) (q/random window-height) (c/nth-mod (:colors palette) (q/random (count palette)))))

(def noise-zoom 0.002)

; changes how zoomed in on curves the thing is (smaller == smoother, generally < 1)
(def step-scalar 1.75)

; changes how much each point jumps

(defn noise-field-radian
  "Get a position dependent radian"
  [x y]
  (* 4 Math/PI (c/noise-field x y noise-zoom)))

(defn polygon-to-baseboard
  [point1 point2 h]
  (apply q/fill (:color point1))
  (q/begin-shape)
  (q/vertex (:x point1) h)
  (q/vertex (:x point1) (:y point1))
  (q/vertex (:x point2) (:y point2))
  (q/vertex (:x point2) h)
  (q/end-shape :close))

; Start of the sketch codes

(defn sketch-setup []
  ; Set color mode to HSB (HSV) instead of default RGB
  (q/color-mode :hsb 360 100 100 1.0)
  (q/no-stroke)
  (apply q/background (:background palette))
  ; Create 2000 particles at the start
  ; (render-field w h)
  (q/no-stroke)
  (sort-by
    (fn [trail]
      (:y (first (:points trail))))
    (map trail (range 0 20))))

(defn sketch-update [trails]
  (->> trails
       (map
         (fn [trail]
           (let [points (:points trail)
                 velocity (c/point-sub (first points) (second points))
                 theta (noise-field-radian (:x (first points)) (:y (first points)))
                 new-velocity {:x (c/average (:x velocity) (Math/cos theta))
                               :y (c/average (:y velocity) (Math/sin theta))}]
             (assoc trail :points
                          (cons (c/point-add (first points) (c/point-scale step-scalar new-velocity))
                                points)))))))

(defn sketch-draw [trails]
  (apply q/background (:background palette))
  ; Reduce each trail to a list of point pairs (ready for cons-ing)
  (as-> trails val
        ; Replace each trail with a list of pairs
        (map
          (fn [trail]
            (partition 2 1 (:points trail)))
          val)
        ; Merge all of the trails' points
        (apply concat val)
        ; Sort them on the y axis
        (sort-by
          (fn
            [point-pair]
            (:y (first point-pair)))
          val)
        ; Hopefully this works
        (doseq [point-pair val]
          (polygon-to-baseboard (first point-pair) (second point-pair) window-height))))

(defn create [{:keys [canvas-id seed]}]
  (q/sketch
    :host canvas-id
    :size [window-width window-height]
    :draw #'sketch-draw
    :setup #'sketch-setup
    :update #'sketch-update
    :middleware [middleware/fun-mode]
    :settings
    (fn []
      (q/random-seed 432)
      (q/noise-seed 432))))

(defn sketch [opts] (create opts))

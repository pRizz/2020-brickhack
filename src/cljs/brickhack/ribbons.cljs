(ns brickhack.ribbons
  (:require [brickhack.common :as c]
            [quil.core :as q]
            [quil.middleware :as middleware]))

(def window-width (.-innerWidth js/window))
(def window-height (.-innerHeight js/window))

(defn nth-mod [coll n]
  (nth coll (mod n (count coll))))

; This-sketch custom code

(def palette-atm (atom nil))

(defn trail
  [id]
  (c/particle-trail id (q/random window-width) (q/random window-height) (rand-nth (:colors @palette-atm))))

; settings constants
(def noise-zoom 0.002)
(def display-field false)

(defn noise-field-radian
  "Get a position dependent radian"
  [x y]
  (* 2 Math/PI (c/noise-field x y noise-zoom)))

(defn render-field-vector
  "Debugging radian noise fields"
  [x y]
  (let [r (noise-field-radian x y)]
    (q/stroke [0 0 0])
    (apply q/line x y (c/coords-with-radian x y r 5))
    (q/ellipse x y 2 2)))

(defn render-field
  "Render a whole field of vectors"
  [width height]
  (doseq [x (range 0 width 10)]
    (doseq [y (range 0 height 10)]
      (render-field-vector x y))))

; Start of the sketch codes

(defn sketch-setup []
  ; Set color mode to HSB (HSV) instead of default RGB
  (q/color-mode :hsb 360 100 100 1.0)
  (q/no-stroke)
  (apply q/background (:background @palette-atm))
  ; Create 2000 particles at the start
  ; (render-field w h)
  (q/no-stroke)
  (sort-by
    (fn [trail]
      (:y (first (:points trail))))
    (map trail (range 0 200))))

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
                          (cons (c/point-add (first points) new-velocity)
                                points)))))))

(defn sketch-draw [trails]
  (apply q/background (:background @palette-atm))
  (doseq [trail trails]
    (apply q/fill (:color trail))
    (q/begin-shape)
    (q/vertex (:x (first (:points trail))) window-height)
    (doseq [point (:points trail)]
      (q/vertex (:x point) (:y point)))
    (q/vertex (:x (last (:points trail))) window-height)
    (q/end-shape :close))
  (when display-field (render-field window-width window-height) (q/no-loop)))

(defn create [{:keys [canvas-id seed]}]
  (reset! palette-atm (nth-mod c/palettes seed))
  (q/sketch
    :host canvas-id
    :size [window-width window-height]
    :draw #'sketch-draw
    :setup #'sketch-setup
    :update #'sketch-update
    :middleware [middleware/fun-mode]
    :settings (fn []
                (q/random-seed seed)
                (q/noise-seed seed))))

(defn sketch [opts]
  (create opts))

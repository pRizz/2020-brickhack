(ns brickhack.core
  (:require [brickhack.common :as c]
            [quil.core :as q]
            [quil.middleware :as middleware]))

(def body (.-body js/document))
(def w (.-clientWidth body))
(def h (.-clientHeight body))

; This-sketch custom code
(def palette (rand-nth c/palettes))

(defn particle
  "Create a particle obj"
  [id]
  {:id        id
   :vx        0
   :vy        0
   :size      2
   :direction 0
   :length    0
   :x         (q/random w)
   :y         (q/random h)
   :color     (rand-nth (:colors palette))})


(def noise-zoom 0.005)

(defn noise-field
  "Generate a perlin noise location dependent value"
  ([x y] (noise-field x y noise-zoom))
  ([x y zoom] (q/noise (* x zoom) (* y zoom))))

(defn noise-field-radian
  "Get a position dependent radian"
  ([x y] (noise-field-radian x y noise-zoom 4))
  ([x y zoom] (noise-field-radian x y zoom 4))
  ([x y zoom scalar] (* scalar Math/PI (noise-field x y zoom))))

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
  (apply q/background (:background palette))
  ; Create 2000 particles at the start
  ; (render-field w h)
  (q/no-stroke)
  (map particle (range 0 2000)))

(defn sketch-update [particles]
  (map
    (fn [p]
      (assoc p
        :x (c/add-with-rollover (:x p) (:vx p) w)
        :y (c/add-with-rollover (:y p) (:vy p) h)
        :length (+ 1 (:length p))
        :direction (noise-field-radian (:x p) (:y p))
        :vx (c/average (:dx p) (Math/cos (:direction p)))
        :vy (c/average (:dy p) (Math/sin (:direction p)))))
    particles))

(defn sketch-draw [particles]
  ; (apply q/background (:background palette))
  (doseq [p particles]
    (apply q/fill (:color p))
    (q/ellipse (:x p) (:y p) (:size p) (:size p))))

(defn create [canvas]
  (q/sketch
    :host canvas
    :size [w h]
    :draw #'sketch-draw
    :setup #'sketch-setup
    :update #'sketch-update
    :middleware [middleware/fun-mode]
    :settings
    (fn []
      (q/random-seed 432)
      (q/noise-seed 432))))

(defonce sketch (create "sketch"))

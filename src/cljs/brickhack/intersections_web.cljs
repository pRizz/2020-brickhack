(ns brickhack.intersections-web
  (:require [brickhack.common :refer [window-width window-height] :as c]
            [quil.core :as q]
            [quil.middleware :as middleware]))

; This-sketch custom code
(def palette (rand-nth c/palettes))

(defn particle-generator [id] (c/particle-generator id window-width window-height palette))

(def noise-zoom 0.002)

(defn noise-field-radian
  "Get a position dependent radian"
  [x y]
  (* 4 Math/PI (c/noise-field x y noise-zoom)))

(defn noise-field-color
  [x y i]
  (nth (:colors palette)
       (c/normalize-to (c/noise-field x y noise-zoom (/ i 1000)) (count (:colors palette)))))

; Start of the sketch codes

(def default-particle-count 2000)

(defn sketch-setup []
  ; Set color mode to HSB (HSV) instead of default RGB
  (q/color-mode :hsb 360 100 100 1.0)
  (q/no-stroke)
  (apply q/background (:background palette))
  ; Create 2000 particles at the start
  ; (render-field w h)
  (q/no-stroke)
  (let [particles (map particle-generator (range default-particle-count))
        anti-particles particles]
    (concat particles anti-particles particles anti-particles)))
  ;(map particle-generator (range default-particle-count)))

(defn sketch-update [particles]
  (->> particles
       (map-indexed
         (fn [index particle]
           (let [v-multiplier (if (>= index default-particle-count) -1 1)
                 direction-multiplier (/ index default-particle-count)]
             (assoc particle
               :x (c/add-with-rollover (:x particle) (:vx particle) window-width)
               :y (c/add-with-rollover (:y particle) (:vy particle) window-height)
               :length (+ 1 (:length particle))
               :color (noise-field-color (:x particle) (:y particle) (:id particle))
               :direction (* direction-multiplier (noise-field-radian (:x particle) (:y particle)))
               :vx (* v-multiplier (Math/cos (:direction particle)))
               :vy (* v-multiplier (Math/sin (:direction particle)))))))
       (filter
         (fn [p]
           (>= 10000 (:length p))))))

(defn sketch-draw [particles]
  ; (apply q/background (:background palette))
  (doseq [p particles]
    (apply q/fill (:color p))
    (q/ellipse (:x p) (:y p) (:size p) (:size p))))

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
      (q/random-seed seed)
      (q/noise-seed seed))))

(defn sketch [opts] (create opts))

(ns generative-art-live.core
  (:require
    [clojure.spec.alpha :as s]
    [reagent.core :as reagent :refer [atom]]
    [reagent.session :as session]
    [reitit.frontend :as reitit]
    [clerk.core :as clerk]
    [accountant.core :as accountant]
    [brickhack.ribbons :as ribbons]
    [brickhack.proper-ribbons :as proper-ribbons]
    [brickhack.intersections :as intersections]
    [brickhack.intersections-dual :as intersections-dual]
    [brickhack.trails :as trails]
    [cljs-material-ui.core :as mui]))

;; -------------------------
;; Routes

(def router
  (reitit/router
    [["/" :index]
     ["/items"
      ["" :items]
      ["/:item-id" :item]]
     ["/about" :about]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

(path-for :about)
;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Welcome to generative-art-live"]
     [:ul
      [:li [:a {:href (path-for :items)} "Items of generative-art-live"]]
      [:li [:a {:href "/broken/link"} "Broken link"]]]]))



(defn items-page []
  (fn []
    [:span.main
     [:h1 "The items of generative-art-live"]
     [:ul (map (fn [item-id]
                 [:li {:name (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (path-for :item {:item-id item-id})} "Item: " item-id]])
               (range 1 60))]]))


(defn item-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of generative-art-live")]
       [:p [:a {:href (path-for :items)} "Back to the list of items"]]])))


(defn about-page []
  (fn [] [:span.main
          [:h1 "About generative-art-live"]]))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :about #'about-page
    :items #'items-page
    :item #'item-page))


;; -------------------------
;; Page mounting component

; add a spec for this; maye defrecord or core.typed

;(s/def ::sketch-fn fn?)
;(s/def ::label string?)
;(s/def ::generator (s/keys :req-un [::sketch-fn ::label]))
;(s/def ::generators (s/coll-of #(s/valid? ::generator %)))

(def generators
  [{:sketch-fn intersections-dual/sketch
    :label     "Intersections Dual"}
   {:sketch-fn ribbons/sketch
    :label     "Ribbons"}
   {:sketch-fn intersections/sketch
    :label     "Intersections"}
   {:sketch-fn proper-ribbons/sketch
    :label     "Proper Ribbons"}])

(defn- toolbar-element [{:keys [on-generator-change on-generator-reset]}]
  (let [selected-generator-index-atom (reagent/atom 0)
        selected-generator-atom (reagent/atom (nth generators 0))]
    (fn [{:keys [on-generator-change on-generator-reset]}]
      [:div {:style {:position         "absolute"
                     :top              20
                     :right            20
                     :padding          20
                     :color            "#ddd"
                     :background-color "gray"
                     :borderRadius     8}}
       [mui/button {:variant  "contained"
                    :color    "primary"
                    :on-click on-generator-reset
                    :style    {}} "Restart"]
       [:div {:style {:height "1em"}}]
       [:div
        [mui/form-control {}
         (mui/input-label {} "Generator")
         (mui/select {:value     @selected-generator-index-atom
                      :on-change (fn [e]
                                   (reset! selected-generator-index-atom (nth generators (-> e .-target .-value)))
                                   (let [generator (nth generators (-> e .-target .-value))]
                                     (reset! selected-generator-atom generator)
                                     (on-generator-change generator)))}
                     (mui/menu-item {:value 0} (:label (nth generators 0))) ; FIXME: use (map)
                     (mui/menu-item {:value 1} (:label (nth generators 1)))
                     (mui/menu-item {:value 2} (:label (nth generators 2))))]]])))

(defn- sketch-element []
  [:div#sketch {:style {:position         "absolute"
                        :top              0
                        :bottom           0
                        :left             0
                        :right            0
                        :background-color "black"}}])

(defn current-page []
  (let [seed 1234
        current-generator-atm (reagent/atom (nth generators 0))
        on-generator-reset (fn []
                             (js/setTimeout #((:sketch-fn @current-generator-atm) {:canvas-id "sketch"
                                                                                   :seed      seed})
                                            1))
        initial-sketch-instance (js/setTimeout #((:sketch-fn @current-generator-atm) {:canvas-id "sketch"
                                                                                      :seed      seed})
                                               2500)
        on-generator-change (fn [generator]
                              (reset! current-generator-atm generator)
                              (on-generator-reset))]
    (fn []
      [:<>
       [sketch-element]
       [toolbar-element {:on-generator-reset  (fn []
                                                (on-generator-reset)
                                                (prn "reset gen"))
                         :on-generator-change (fn [generator]
                                                (on-generator-change generator)
                                                (prn "new val" generator))}]])))


;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (let [match (reitit/match-by-path router path)
             current-page (:name (:data match))
             route-params (:path-params match)]
         (reagent/after-render clerk/after-render!)
         (session/put! :route {:current-page (page-for current-page)
                               :route-params route-params})
         (clerk/navigate-page! path)))

     :path-exists?
     (fn [path]
       (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root)
  (aset (-> js/document .-documentElement .-style) "backgroundColor" "black"))

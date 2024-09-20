(ns cloje
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [io.github.humbleui.ui :as ui]
   [io.github.humbleui.util :as util]
   [io.github.humbleui.canvas :as canvas]
   [io.github.humbleui.window :as window])
  (:import
   [java.io File]))

(def SIZE 100)
(def !sands (atom {}))

(defn init []
  (reset! !sands
          (into {} (for [x (range SIZE),
                         y (range SIZE)]
                     [[x y] (rand-nth [:empty :sand :empty :empty])]))))

(init)
(def sand-fill (ui/paint {:fill "ff80de"}))

;; atom for a numerb
(def !counter (atom 0))

;; set everything to sand
(defn lol []
  (swap! !sands
         (fn [sands]
           (reduce (fn [new-sands [[x, y] element]]
                     (let [side (rand-nth [dec inc])
                           below (get new-sands [x (inc y)] :sand)
                           below-side (get new-sands [(side x) (inc y)] :sand)]
                       (cond
                          ;; if below is empty, move down
                         (and (= element :sand) (= below :empty))
                         (assoc new-sands [x y] :empty,
                                [x (inc y)] :sand)
                         ;; if there is empty space down and to the side, move down and to the right
                         (and (= element :sand) (= below-side :empty))
                         (assoc new-sands [x y] :empty,
                                [(side x) (inc y)] :sand)
                         :else new-sands)))

                   sands
                   sands))))


(def !dimensions (atom {:width 0 :height 0}))

(defn tick [ctx canvas size]
  (swap! !counter inc)
  (let [counter @!counter]
    (when (zero? (mod counter 2)) (lol))
    (let [w (:width size)
          h (:height size)
          dx (/ w SIZE)
          dy (/ h SIZE)
          sands @!sands]
      (reset! !dimensions {:width w :height h})
      (doseq [[[x, y] element] sands]
        (case element
          :empty nil
          :sand (canvas/draw-rect canvas (util/rect-xywh (* x dx) (* y dy) dx dy) sand-fill)))))
  (window/request-frame (:window ctx)))



; {:event :mouse-move, :x -11, :y 458, :buttons #{}, :modifiers #{}}
; {:event :mouse-move, :x 506, :y 1301, :buttons #{:primary}, :modifiers #{}}

(defn on-event [_ctx e]
  (when (and (= (:event e) :mouse-move)
             (contains? (:buttons e) :primary))
    (prn "DRAW SAND!!!")
    (let [x (int (/ (:x e) (-> @!dimensions :width (/ SIZE))))
          y (int (/ (:y e) (-> @!dimensions :height (/ SIZE))))]
      (swap! !sands assoc [x y] :sand))))



#_{:clj-kondo/ignore [:unresolved-symbol]}
(ui/defcomp app []
  [ui/canvas
   {:on-paint tick
    :on-event on-event}])

(defn -main [& args]
  (ui/start-app!
   #_{:clj-kondo/ignore [:unresolved-symbol]}
   (ui/window
    {:title    "Pink sand"
     :mac-icon "resources/icon.icns"}
    #'app)))

(comment
  ;; Start app
  (-main)

  ;; View docs
  ((requiring-resolve 'io.github.humbleui.docs/open!)))

(ns cloje
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [io.github.humbleui.ui :as ui]
   [io.github.humbleui.util :as util]
   [io.github.humbleui.canvas :as canvas])
  (:import
   [java.io File]))

(def SIZE 50)
(def !sands (atom []))

(defn init []
  (reset! !sands
          (vec (for [x (range SIZE)
                y (range SIZE)]
            (rand-nth [:empty :sand])))))

(init)
(def sand-fill (ui/paint {:fill "ff80de"}))

(defn on-paint [ctx canvas size] 
  (let [w (:width size)
		h (:height size)
		dx (/ w SIZE)
		dy (/ h SIZE) 
		sands @!sands] 
	(doseq [x (range SIZE)
			y (range SIZE)]
	  (let [index (+ (* y SIZE) x)]
  	   (case (get sands index)
		 :empty nil 
     		 :sand (canvas/draw-rect canvas (util/rect-xywh (* x dx) (* y dy) dx dy) sand-fill))))))
  	  

(defn on-event [_ctx e])

(ui/defcomp app []
  [ui/canvas
   {:on-paint on-paint
    :on-event on-event}])

(defn -main [& args]
  (ui/start-app!
   (ui/window
    {:title    "Pink sand"
     :mac-icon "resources/icon.icns"}
    #'app)))

(comment
  ;; Start app
  (-main)

  ;; View docs
  ((requiring-resolve 'io.github.humbleui.docs/open!)))

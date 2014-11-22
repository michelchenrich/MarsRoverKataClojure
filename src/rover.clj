(ns rover)

(defn- get-grid [limit rover]
  (get (get rover :grid) limit))

(defn- limit [rover axis]
  (case axis
    :y (get-grid :height rover)
    :x (get-grid :width rover)))

(defn- move-forward [rover axis forward-sign]
  (let [old-position (get rover axis)
        wanted-position (forward-sign old-position 1)
        border-position (- (limit rover axis) 1)
        new-position (if (> wanted-position -1) wanted-position border-position)]
    (assoc rover axis new-position)))

(defn- make-actions [axis forward-sign left-turner right-turner]
  (let [backward-sign (if (= + forward-sign) - +)]
    {:forward (fn [rover] (move-forward rover axis forward-sign))
     :backward (fn [rover] (assoc rover axis (backward-sign (get rover axis) 1)))
     :turn-left (fn [rover] (assoc rover :direction (left-turner)))
     :turn-right (fn [rover] (assoc rover :direction (right-turner)))}))

;the anonymous functions are here to avoid cyclic dependencies, since the make-actions function has to determine the
;direction to turn, but make-actions has to be called in order to have the directions, so we have to make then be lazily
;evaluated
(def ^:private directions
  {:north {:name :north :actions (make-actions :y - (fn [] (get directions :west)) (fn [] (get directions :east)))}
   :south {:name :south :actions (make-actions :y + (fn [] (get directions :east)) (fn [] (get directions :west)))}
   :west {:name :west :actions (make-actions :x - (fn [] (get directions :south)) (fn [] (get directions :north)))}
   :east {:name :east :actions (make-actions :x + (fn [] (get directions :north)) (fn [] (get directions :south)))}})

(defn make
  [x y direction-name grid]
  {:x x :y y :direction (get directions direction-name) :grid grid})

(defn- execute [rover command]
  ((get (get (get rover :direction) :actions) command) rover))

(defn execute-many [rover commands]
  (reduce execute rover commands))
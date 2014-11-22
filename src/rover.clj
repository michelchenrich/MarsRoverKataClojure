(ns rover)

(defn- limit-of [rover axis]
  (case axis
    :x (-> rover (get :grid) (get :width))
    :y (-> rover (get :grid) (get :height))))

(defn- wrap-position [wanted-position limit-position]
  (if (< wanted-position 0)
    limit-position
    (if (> wanted-position limit-position)
      0
      wanted-position)))

(defn- move [rover sign axis]
  (let [wanted (-> rover (get axis) (sign 1))
        limit (-> rover (limit-of axis) (- 1))]
    (assoc rover axis (wrap-position wanted limit))))

(defn- turn [rover to-side]
  (assoc rover :direction (to-side)))

(defn- make-handlers [in-axis forward to-the-left to-the-right]
  (let [backward (if (= + forward) - +)]
    {:forward (fn [rover] (move rover forward in-axis))
     :backward (fn [rover] (move rover backward in-axis))
     :turn-left (fn [rover] (turn rover to-the-left))
     :turn-right (fn [rover] (turn rover to-the-right))}))

;the anonymous functions are needed to avoid cyclic dependencies, because the make-handlers function has to determine to
;which direction to turn, but it also has to be called in order to have the directions, so we have to make them be
;lazily evaluated at the moment of turning
(def ^:private directions
  {:north {:name :north :handlers (make-handlers :y - (fn [] (get directions :west)) (fn [] (get directions :east)))}
   :south {:name :south :handlers (make-handlers :y + (fn [] (get directions :east)) (fn [] (get directions :west)))}
   :west {:name :west :handlers (make-handlers :x - (fn [] (get directions :south)) (fn [] (get directions :north)))}
   :east {:name :east :handlers (make-handlers :x + (fn [] (get directions :north)) (fn [] (get directions :south)))}})

(defn make [x y direction grid]
  {:x x :y y :direction (get directions direction) :grid grid})

(defn- execute [rover command]
  ((-> rover (get :direction) (get :handlers) (get command)) rover))

(defn execute-many [rover commands]
  (reduce execute rover commands))
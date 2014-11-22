(ns rover)

(defn- limit-of [rover axis]
  (case axis
    :x (-> rover (get :grid) (get :width))
    :y (-> rover (get :grid) (get :height))))

(defn- wrap-position [wanted-position upper-limit]
  (let [lower-limit 0]
    (cond
      (< wanted-position lower-limit) upper-limit
      (> wanted-position upper-limit) lower-limit
      :else wanted-position)))

(defn- move [rover sign axis]
  (let [wanted (-> rover (get axis) (sign 1))
        limit (-> rover (limit-of axis) (- 1))]
    (assoc rover axis (wrap-position wanted limit))))

(declare directions)

(defn- turn [rover side]
  (assoc rover :direction (get directions side)))

(defn- make-handlers [in-axis forward to-the-left to-the-right]
  (let [backward (if (= + forward) - +)]
    {:forward (fn [rover] (move rover forward in-axis))
     :backward (fn [rover] (move rover backward in-axis))
     :turn-left (fn [rover] (turn rover to-the-left))
     :turn-right (fn [rover] (turn rover to-the-right))}))

(def ^:private directions
  {:north {:name :north :handlers (make-handlers :y - :west :east)}
   :south {:name :south :handlers (make-handlers :y + :east :west)}
   :west {:name :west :handlers (make-handlers :x - :south :north)}
   :east {:name :east :handlers (make-handlers :x + :north :south)}})

(defn make [x y direction grid]
  {:x x :y y :direction (get directions direction) :grid grid})

(defn execute [rover command]
  ((-> rover (get :direction) (get :handlers) (get command)) rover))

(defn execute-many [rover commands]
  (reduce execute rover commands))
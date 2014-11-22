(ns rover-tests
  (:use clojure.test))

(defn- make-actions [axis forward-sign left-turner right-turner]
  (let [backward-sign (if (= + forward-sign) - +)]
    {:forward (fn [rover] (assoc rover axis (forward-sign (get rover axis) 1)))
     :backward (fn [rover] (assoc rover axis (backward-sign (get rover axis) 1)))
     :turn-left (fn [rover] (assoc rover :direction (left-turner)))
     :turn-right (fn [rover] (assoc rover :direction (right-turner)))}))

(def ^:private directions
  {:north {:name :north :actions (make-actions :y - (fn [] (get directions :west)) (fn [] (get directions :east)))}
   :south {:name :south :actions (make-actions :y + (fn [] (get directions :east)) (fn [] (get directions :west)))}
   :west {:name :west :actions (make-actions :x - (fn [] (get directions :south)) (fn [] (get directions :north)))}
   :east {:name :east :actions (make-actions :x + (fn [] (get directions :north)) (fn [] (get directions :south)))}})

(defn make-rover
  [x y direction-name grid]
  {:x x :y y :direction (get directions direction-name) :grid grid})

(defn- execute [rover command]
  ((get (get (get rover :direction) :actions) command) rover))

(defn execute-many [rover commands]
  (reduce execute rover commands))

(defn- assert-position [rover x y direction-name]
  (is (= x (get rover :x)))
  (is (= y (get rover :y)))
  (is (= direction-name (get (get rover :direction) :name))))

(deftest rover-tests
  (let [grid {:width 10 :height 10}]
    (testing "initial position"
      (assert-position (make-rover 5 5 :south grid) 5 5 :south))

    (testing "facing north"
      (let [rover (make-rover 1 1 :north grid)]
        (testing "moving forward"
          (assert-position (execute-many rover '(:forward)) 1 0 :north))
        (testing "moving backward"
          (assert-position (execute-many rover '(:backward)) 1 2 :north))
        (testing "turning left"
          (assert-position (execute-many rover '(:turn-left)) 1 1 :west))
        (testing "turning right"
          (assert-position (execute-many rover '(:turn-right)) 1 1 :east))))

    (testing "facing south"
      (let [rover (make-rover 1 1 :south grid)]
        (testing "moving forward"
          (assert-position (execute-many rover '(:forward)) 1 2 :south))
        (testing "moving backward"
          (assert-position (execute-many rover '(:backward)) 1 0 :south))
        (testing "turning left"
          (assert-position (execute-many rover '(:turn-left)) 1 1 :east))
        (testing "turning right"
          (assert-position (execute-many rover '(:turn-right)) 1 1 :west))))

    (testing "facing east"
      (let [rover (make-rover 1 1 :east grid)]
        (testing "moving forward"
          (assert-position (execute-many rover '(:forward)) 2 1 :east))
        (testing "moving backward"
          (assert-position (execute-many rover '(:backward)) 0 1 :east))
        (testing "turning left"
          (assert-position (execute-many rover '(:turn-left)) 1 1 :north))
        (testing "turning right"
          (assert-position (execute-many rover '(:turn-right)) 1 1 :south))))

    (testing "facing west"
      (let [rover (make-rover 1 1 :west grid)]
        (testing "moving forward"
          (assert-position (execute-many rover '(:forward)) 0 1 :west))
        (testing "moving backward"
          (assert-position (execute-many rover '(:backward)) 2 1 :west))
        (testing "turning left"
          (assert-position (execute-many rover '(:turn-left)) 1 1 :south))
        (testing "turning right"
          (assert-position (execute-many rover '(:turn-right)) 1 1 :north))))

    (testing "many commands"
      (let [rover (make-rover 1 1 :north grid)
            commands '(:forward :turn-right :forward :turn-right :forward :turn-right :forward)]
        (assert-position (execute-many rover commands) 1 1 :west)))))

(run-tests 'rover-tests)
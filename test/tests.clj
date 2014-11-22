(ns rover-tests
  (:use clojure.test)
  (:require rover))

(defn- assert-position [rover x y direction-name]
  (is (= x (get rover :x)))
  (is (= y (get rover :y)))
  (is (= direction-name (get (get rover :direction) :name))))

(deftest rover-tests
  (let [grid {:width 10 :height 10}]
    (testing "initial position"
      (assert-position (rover/make 5 5 :south grid) 5 5 :south))

    (testing "facing north"
      (let [rover (rover/make 1 1 :north grid)]
        (testing "moving forward"
          (assert-position (rover/execute-many rover '(:forward)) 1 0 :north))
        (testing "moving backward"
          (assert-position (rover/execute-many rover '(:backward)) 1 2 :north))
        (testing "turning left"
          (assert-position (rover/execute-many rover '(:turn-left)) 1 1 :west))
        (testing "turning right"
          (assert-position (rover/execute-many rover '(:turn-right)) 1 1 :east))
        (testing "moving beyond the limit"
          (assert-position (rover/execute-many rover '(:forward :forward)) 1 9 :north))))

    (testing "facing south"
      (let [rover (rover/make 1 1 :south grid)]
        (testing "moving forward"
          (assert-position (rover/execute-many rover '(:forward)) 1 2 :south))
        (testing "moving backward"
          (assert-position (rover/execute-many rover '(:backward)) 1 0 :south))
        (testing "turning left"
          (assert-position (rover/execute-many rover '(:turn-left)) 1 1 :east))
        (testing "turning right"
          (assert-position (rover/execute-many rover '(:turn-right)) 1 1 :west))))

    (testing "facing east"
      (let [rover (rover/make 1 1 :east grid)]
        (testing "moving forward"
          (assert-position (rover/execute-many rover '(:forward)) 2 1 :east))
        (testing "moving backward"
          (assert-position (rover/execute-many rover '(:backward)) 0 1 :east))
        (testing "turning left"
          (assert-position (rover/execute-many rover '(:turn-left)) 1 1 :north))
        (testing "turning right"
          (assert-position (rover/execute-many rover '(:turn-right)) 1 1 :south))))

    (testing "facing west"
      (let [rover (rover/make 1 1 :west grid)]
        (testing "moving forward"
          (assert-position (rover/execute-many rover '(:forward)) 0 1 :west))
        (testing "moving backward"
          (assert-position (rover/execute-many rover '(:backward)) 2 1 :west))
        (testing "turning left"
          (assert-position (rover/execute-many rover '(:turn-left)) 1 1 :south))
        (testing "turning right"
          (assert-position (rover/execute-many rover '(:turn-right)) 1 1 :north))))

    (testing "many commands"
      (let [rover (rover/make 1 1 :north grid)
            commands '(:forward :turn-right :forward :turn-right :forward :turn-right :forward)]
        (assert-position (rover/execute-many rover commands) 1 1 :west)))))

(run-tests 'rover-tests)
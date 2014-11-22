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
          (assert-position (rover/execute rover :forward) 1 0 :north))
        (testing "moving backward"
          (assert-position (rover/execute rover :backward) 1 2 :north))
        (testing "turning left"
          (assert-position (rover/execute rover :turn-left) 1 1 :west))
        (testing "turning right"
          (assert-position (rover/execute rover :turn-right) 1 1 :east))))

    (testing "facing south"
      (let [rover (rover/make 1 1 :south grid)]
        (testing "moving forward"
          (assert-position (rover/execute rover :forward) 1 2 :south))
        (testing "moving backward"
          (assert-position (rover/execute rover :backward) 1 0 :south))
        (testing "turning left"
          (assert-position (rover/execute rover :turn-left) 1 1 :east))
        (testing "turning right"
          (assert-position (rover/execute rover :turn-right) 1 1 :west))))

    (testing "facing east"
      (let [rover (rover/make 1 1 :east grid)]
        (testing "moving forward"
          (assert-position (rover/execute rover :forward) 2 1 :east))
        (testing "moving backward"
          (assert-position (rover/execute rover :backward) 0 1 :east))
        (testing "turning left"
          (assert-position (rover/execute rover :turn-left) 1 1 :north))
        (testing "turning right"
          (assert-position (rover/execute rover :turn-right) 1 1 :south))))

    (testing "facing west"
      (let [rover (rover/make 1 1 :west grid)]
        (testing "moving forward"
          (assert-position (rover/execute rover :forward) 0 1 :west))
        (testing "moving backward"
          (assert-position (rover/execute rover :backward) 2 1 :west))
        (testing "turning left"
          (assert-position (rover/execute rover :turn-left) 1 1 :south))
        (testing "turning right"
          (assert-position (rover/execute rover :turn-right) 1 1 :north))))

    (testing "many commands"
      (let [rover (rover/make 1 1 :north grid)
            commands '(:forward :turn-right :forward :turn-right :forward :turn-right :forward)]
        (assert-position (rover/execute-many rover commands) 1 1 :west)))

    (testing "wrapping by"
      (testing "facing north and moving forward"
        (assert-position (rover/execute (rover/make 0 0 :north grid) :forward) 0 9 :north))
      (testing "facing north and moving backward"
        (assert-position (rover/execute (rover/make 0 9 :north grid) :backward) 0 0 :north))
      (testing "facing south and moving forward"
        (assert-position (rover/execute (rover/make 0 9 :south grid) :forward) 0 0 :south))
      (testing "facing south and moving backward"
        (assert-position (rover/execute (rover/make 0 0 :south grid) :backward) 0 9 :south))
      (testing "facing east and moving forward"
        (assert-position (rover/execute (rover/make 9 0 :east grid) :forward) 0 0 :east))
      (testing "facing east and moving backward"
        (assert-position (rover/execute (rover/make 0 0 :east grid) :backward) 9 0 :east))
      (testing "facing west and moving forward"
        (assert-position (rover/execute (rover/make 0 0 :west grid) :forward) 9 0 :west))
      (testing "facing west and moving backward"
        (assert-position (rover/execute (rover/make 9 0 :west grid) :backward) 0 0 :west)))))

(run-tests 'rover-tests)
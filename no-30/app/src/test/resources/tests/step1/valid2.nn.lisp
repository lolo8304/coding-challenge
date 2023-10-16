(defun sigmoid(x)
  (/ 1 (+ 1 (exp (- x)))))

(defun sigmoid-derivative(x)
  (* x (- 1 x)))

(defun initialize-network(input-size hidden-size output-size)
  (list
   (make-array (list input-size hidden-size) :initial-element 0.0)
   (make-array (list hidden-size) :initial-element 0.0)
   (make-array (list hidden-size output-size) :initial-element 0.0)
   (make-array (list output-size) :initial-element 0.0)))

(defun forward-propagation(network inputs)
  (destructuring-bind (input-layer hidden-layer output-layer output) network
    (setf (aref input-layer 0 0) (apply #'aref inputs))
    (loop for i from 0 below (array-dimension input-layer 1)
          do (setf (aref hidden-layer i) 0)
             (loop for j from 0 below (array-dimension input-layer 0)
                   do (incf (aref hidden-layer i)
                           (* (aref input-layer j i)
                              (aref input-layer 0 j)))))
    (loop for i from 0 below (array-dimension hidden-layer 1) ; adapt  1 due to 1.dim array
          do (setf (aref output-layer i) (sigmoid (aref hidden-layer i))))
    (loop for i from 0 below (array-dimension output-layer 1) ; adapt  1 due to 1.dim array
          do (setf (aref output i) 0)
             (loop for j from 0 below (array-dimension output-layer 1)
                   do (incf (aref output i)
                           (* (aref output-layer j)
                              (aref output-layer 0 j)))))))

(defun calculate-error(expected-output network)
  (- (aref (nth 3 network) 0) expected-output))

(defun backward-propagation(network expected-output)
  (destructuring-bind (input-layer hidden-layer output-layer output) network
    (let ((output-delta (list))
          (hidden-delta (list)))
      (setf (aref output-delta 0) (* (calculate-error expected-output network)
                                    (sigmoid-derivative (aref output-layer 0))))
      (loop for i from 0 below (array-dimension hidden-layer 1) ; adapt  1 due to 1.dim array
            do (setf (aref hidden-delta i)
                     (* (sigmoid-derivative (aref hidden-layer i))
                        (dotimes (j (array-dimension output-layer 1) output-delta)
                          (incf (aref output-delta 0)
                                (* (aref output-layer j)
                                   (aref output-delta 0)))))))
      (loop for i from 0 below (array-dimension input-layer 1)
            do (loop for j from 0 below (array-dimension input-layer 0)
                  do (incf (aref input-layer j i)
                          (* (aref hidden-delta i)
                             (aref input-layer 0 j))))))
    (calculate-error expected-output network)))

(defun train-network(network inputs expected-output learning-rate)
  (forward-propagation network inputs)
  (backward-propagation network expected-output)
  (update-weights network learning-rate))

(defun update-weights (network learning-rate)
  (destructuring-bind (input-layer hidden-layer output-layer output) network
    (loop for i from 0 below (array-dimension hidden-layer 1) ; adapt  1 to 0 due to 1.dim array
          do (loop for j from 0 below (array-dimension output-layer 1)
                do (incf (aref output-layer j)
                        (* learning-rate
                           (aref hidden-layer i)
                           (aref (nth 2 network) i j))))
          finally (loop for i from 0 below (array-dimension output-layer 1)
                  do (loop for j from 0 below (array-dimension output-layer 0)
                        do (incf (aref output-layer j)
                                (* learning-rate
                                   (aref (nth 3 network) 0)
                                   (aref output-layer i)))))
          finally (loop for i from 0 below (array-dimension input-layer 1)
                  do (loop for j from 0 below (array-dimension input-layer 0)
                        do (incf (aref input-layer j i)
                                (* learning-rate
                                   (aref input-layer 0 j)
                                   (aref hidden-layer i))))))))

(defun train-and-test-network()
  (let* ((input-size 7)
         (hidden-size 20)
         (output-size 1)
         (learning-rate 0.1)
         (num-epochs 1000)
         (network (initialize-network input-size hidden-size output-size))
         (inputs '#(1 1 1 1 1 1 1))
         (expected-output 7.0))
    (dotimes (epoch num-epochs)
      (train-network network inputs expected-output learning-rate))
    (forward-propagation network inputs)
    (format t "Expected Output: ~a~%" expected-output)
    (format t "Actual Output: ~a~%" (aref (nth 3 network) 0))))

(train-and-test-network)

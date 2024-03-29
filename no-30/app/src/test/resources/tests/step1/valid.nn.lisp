;;; Define the activation function (sigmoid)
(defun sigmoid(x)
  (/ 1.0 (+ 1 (exp (- x)))))

;;; Initialize weights and biases for the hidden and output layers
(setq hidden-weights (make-array '(7 20) :initial-contents (loop repeat 140 collect (random 1.0)))
      hidden-biases (make-array '(20) :initial-contents (loop repeat 20 collect (random 1.0)))
      output-weights (make-array '(20 1) :initial-contents (loop repeat 20 collect (random 1.0)))
      output-bias (random 1.0))

;;; Define the forward pass
(defun forward-pass(inputs)
  (let* ((hidden-activations (make-array '(20)  :initial-contents (loop repeat 20 collect (random 0.00001)) ))
         (output-activation 0.000001))
    ;; Compute hidden layer activations
    (dotimes (i 20)
      (setf (aref hidden-activations i)
            (sigmoid (+ (dotimes (j 7 (+ 1 j)) (* (aref inputs j) (aref hidden-weights j i)))
                        (aref hidden-biases i)))))

    ;; Compute output activation
    (setf output-activation
          (sigmoid (+ (dotimes (i 20 (+ 1 i)) (* (aref hidden-activations i) (aref output-weights i 0)))
                      output-bias)))
    (format t "output-activation = ~A~%" output-activation)

    output-activation))

;;; Train the neural network (example training loop)
(defun train-network(inputs target)
  (let* ((learning-rate 0.1)
         (hidden-activations (make-array '(20) :initial-element 0.0000000000))
         (output-activation 0.0))
    ;; Forward pass
    (dotimes (i 20)
      (setf (aref hidden-activations i)
            (sigmoid (+ (dotimes (j 7 (+ 1 j)) (* (aref inputs j) (aref hidden-weights j i)))
                        (aref hidden-biases i)))))
    (format t "hidden-activations = ~A~%" hidden-activations)

    (setf output-activation
          (sigmoid (+ (dotimes (i 20 (+ 1 i)) (* (aref hidden-activations i) (aref output-weights i 0)))
                      output-bias)))
    (format t "output-activation = ~A~%" output-activation)

    ;; Compute the error
    (let* ((error (- target output-activation))
           (output-delta (* error output-activation (- 1 output-activation)))
           (hidden-errors (make-array '(20)))
           (hidden-deltas (make-array '(20)))
           (new-output-weights (make-array '(20 1)))
           (new-hidden-weights (make-array '(7 20)))
           (new-output-bias 0.000001)
           (new-hidden-bias (make-array '(20) :initial-element 0.000001)))
      (format t "error = ~A~%" error)
      (format t "output-delta = ~A~%" output-delta)
      ;; Calculate errors and deltas
      (dotimes (i 20)
        (setf (aref hidden-errors i) (* (aref output-weights i 0) output-delta))
        (setf (aref hidden-deltas i) (* (aref hidden-activations i) (- 1 (aref hidden-activations i)) (aref hidden-errors i))))

      ;; Update output layer weights and bias
      (dotimes (i 20)
        (setf (aref new-output-weights i 0) (+ (aref output-weights i 0) (* learning-rate output-delta (aref hidden-activations i))))
        (setf new-output-bias (+ new-output-bias (* learning-rate output-delta))))

      ;; Update hidden layer weights and bias
      (dotimes (i 7)
        (dotimes (j 20)
          (setf (aref new-hidden-weights i j) (+ (aref hidden-weights i j) (* learning-rate (aref hidden-deltas j) (aref inputs i))))
          (setf (aref new-hidden-bias j) (+ (aref hidden-biases j) (* learning-rate (aref hidden-deltas j))))))

      ;; Update weights and biases
      (setf output-weights new-output-weights
            hidden-weights new-hidden-weights
            output-bias new-output-bias
            hidden-biases new-hidden-bias))))
      (format t "output-weights ~A~%" output-weights)
      (format t "hidden-weights ~A~%" hidden-weights)
      (format t "output-bias ~A~%" output-bias)
      (format t "hidden-biases ~A~%" hidden-biases)

;;; Test the neural network
(let (
      (inputs0 '#(1 1 1 1 1 1 0))
      (target0 0)
      (inputs1 '#(0 1 1 0 0 0 0))
      (target1 1)
      (inputs2 '#(1 1 0 1 1 0 1))
      (target2 2)
      (inputs3 '#(1 1 1 1 0 0 1))
      (target3 3)
      (inputs4 '#(0 1 1 0 0 1 1))
      (target4 4)
      (inputs5 '#(1 0 1 1 0 1 1))
      (target5 5)
      (inputs6 '#(1 0 1 1 1 1 1))
      (target6 6)
      (inputs7 '#(1 1 1 0 0 0 0))
      (target7 7)
      (inputs8 '#(1 1 1 1 1 1 1))
      (target8 8)
      (inputs9 '#(1 1 1 1 0 1 1))
      (target9 9)
     )
  (format t "Input: ~A~%" inputs3)
  (format t "Target: ~A~%" target3)
  (format t "Prediction: ~A~%" (forward-pass inputs3))
  (format t "Training...~%")
  ;; Train the network with the same input and target
  (dotimes (i 100)  ; Training loop
    (train-network inputs0 target0)
    (train-network inputs1 target1)
    (train-network inputs2 target2)
    (train-network inputs3 target3)
    (train-network inputs4 target4)
    (train-network inputs5 target5)
    (train-network inputs6 target6)
    (train-network inputs7 target7)
    (train-network inputs8 target8)
    (train-network inputs9 target9)
  )
  (format t "Trained Prediction: ~A~%" (forward-pass inputs3)))
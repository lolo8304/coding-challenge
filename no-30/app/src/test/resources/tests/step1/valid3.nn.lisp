(defun sigmoid (x)
  (/ 1.0 (+ 1.0 (expt 2.718281828459045 (- x)))))

(defun sigmoid-derivative (x)
  (* x (- 1.0 x)))

(defun dot-product (vec1 vec2)
  (reduce #'+ (map 'list #'* vec1 vec2)))

(defun forward-propagation (input weights1 weights2)
  (let* ((hidden-layer (mapcar #'(lambda (w) (sigmoid (dot-product input w))) weights1))
         (output (sigmoid (dot-product hidden-layer weights2))))
    (list hidden-layer output)))

(defun backward-propagation (input target output weights1 weights2 learning-rate)
  (let* ((delta-output (* (- target output) (sigmoid-derivative output)))
         (delta-hidden-layer (mapcar #'(lambda (x) (* delta-output (sigmoid-derivative x))) weights2))
         (new-weights2 (mapcar #'(lambda (w) (+ w (* delta-output learning-rate))) weights2))
         (new-weights1 (loop for i from 0 to (- (length weights1) 1)
                            collect
                            (mapcar #'(lambda (x)
                                        (+ (nth i x)
                                           (* (nth i input)
                                              (nth i delta-hidden-layer)
                                              learning-rate)))
                                    (nth i weights1))))
    (values new-weights1 new-weights2))))

(defun train-network (input-size hidden-size output-size num-epochs learning-rate)
  (let* ((input-layer (make-list input-size :initial-element 0.0))
         (weights1 (loop repeat hidden-size collect
                         (loop repeat input-size collect (random 1.0))))
         (weights2 (loop repeat output-size collect (random 1.0)))
         (examples (loop repeat 10 collect
                     (list (make-list input-size :initial-element 1.0)
                           (random 1.0))))
         (targets (loop for example in examples
                     collect (second example))))
    (dotimes (epoch num-epochs)
      (format t "Epoch ~a~%" (1+ epoch))
      (loop for i from 0 to (- (length examples) 1)
            for example = (nth i examples)
            for target = (nth i targets)
            do (multiple-value-setq (weights1 weights2)
                 (backward-propagation example target
                                      (second (forward-propagation example weights1 weights2))
                                      weights1 weights2
                                      learning-rate)))
      (format t "Weights1: ~a~%" weights1)
      (format t "Weights2: ~a~%" weights2))
    (values weights1 weights2)))

(defun test-network (input weights1 weights2)
  (second (forward-propagation input weights1 weights2)))

;; Example usage:
(let* ((input-size 7)
       (hidden-size 20)
       (output-size 1)
       (num-epochs 1000)
       (learning-rate 0.1)
       (final-weights (train-network input-size hidden-size output-size num-epochs learning-rate))
       (input (make-list input-size :initial-element 1.0))
       (output (test-network input (car final-weights) (cdr final-weights))))
  (format t "Input: ~a~%" input)
  (format t "Final Output: ~a~%" output))

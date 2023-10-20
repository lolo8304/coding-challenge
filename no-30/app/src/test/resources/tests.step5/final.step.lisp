(defun hello () (format t "Hello Coding Challenge World~%"))

(defun doublen (n)
  (* n 2))

(defun fib (n)
  (if (< n 2)
      n
      (+ (fib (- n 1))
         (fib (- n 2)))))

(defun fact (n)
  (if (<= n 1)
    1
    (* n (fact (- n 1)))))

;; own built in to define fast and fib as "pure functions" to cash results by arguments
;; if not then recursive operation of fib and fact are killing CPU and performance
(pure fact fib)

(hello)

(format t "The double of 5 is ~D~%" (doublen 5))

(format t "Factorial of 5 is ~D~%" (fact 5))

(format t "The 7th number of the Fibonacci sequence is ~D~%" (fib 7))

(format t "The 42th number of the Fibonacci sequence is ~D~%" (fib 42))
(format t "The 100th number of the Fibonacci sequence is ~D~%" (fib 100))

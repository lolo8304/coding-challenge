# Challenge 30 - Write Your Own Lisp Interpreter

This is the challenge 30 from the Coding Challenges by John Crickett https://codingchallenges.fyi/challenges/challenge-lisp.

## Description

this solution implement a Lisp interpreter in Java:

- full tokenizer and parser based on clisp standard
- implemented interpreter using a stack with all variable possiblities
- implemented 34 (!!!) built-ins and easy to implement new ones
- many testcases to validate parser
- implemented a cache for pure functions to speed up execution (e.g. fact, fib, ...) if recursion algorithms are heavily used
- implemented multi-dimensional arrays

## Build

Open Intellij or any other IDE with gradle support. run "installDist"

## Tests

Run test cases using gradle "test" command. checkout many test files for scanner, parser, runtime, builtin-tests, etc

## Usage

### Commandline parameters

I am using picocli to parse arguments

```bash
./lisp.sh -h
Usage: lisp [-hV] [-c=<command>] [-f=<fileName>]
This challenge is to build Your Own Lisp Interpreter
  -c=<command>     -c specifies the command executed
  -f=<fileName>    -f specifies a file name to load
  -h, --help       Show this help message and exit.
  -V, --version    Print version information and exit.

```

### run interactive console 

```common lisp
./repl.sh 

clisp>> (+ 42 43)
85
clisp>> 
```

### run a file

this will load a file and execute it and run a command based on the same runtime.

```bash
./lisp.sh -f=app/src/test/resources/tests.step5/final.step.lisp -c="(fib 42)"
Load from file app/src/test/resources/tests.step5/final.step.lisp
Hello Coding Challenge World
The double of 5 is 10
Factorial of 5 is 120
The 7th number of the Fibonacci sequence is 13
The 42th number of the Fibonacci sequence is 267914296
The 100th number of the Fibonacci sequence is 2147483647
267914296
```

if you dont pass a command, you will end up in the REPL console

```bash
./lisp.sh -f=app/src/test/resources/tests.step5/final.step.lisp
Load from file app/src/test/resources/tests.step5/final.step.lisp
Hello Coding Challenge World
The double of 5 is 10
Factorial of 5 is 120
The 7th number of the Fibonacci sequence is 13
The 42th number of the Fibonacci sequence is 267914296
The 100th number of the Fibonacci sequence is 2147483647
clisp>> (fib 101)
2147483647
clisp>> 

```

### checkou builtin: pure
I have implemented a pure function as a built-in to be able to cash results. The two defined examples from John fib and fact had been implemented in the recursive way and for large numbers, it will kill your machine.  

The idea behind the caching of a pure function is that the pure function only uses information via its parameters. This means we could cache the result with same parameters. 

run this first in interactive mode: 
```lisp
(defun fib (n)
  (if (< n 2)
      n
      (+ (fib (- n 1))
         (fib (- n 2)))))
```


try without pure : kills your PC
```lisp
(fib 100)
```

or with plugin. The plugin is embedded in the run time interpreter to return cached results with same parameters  

```lisp
(pure fib)
(fib 100)
```


## TODO

- I wanted to implement a Neuronal network with forward and backward propagation. Lisp code from ChatGPT + adaptations
- I have implemented many built-ins for this, but still not really working.
- multidimensional are supported but not while using the tokenizer: #2A((1 2)(2 2))


## Final note

It was awesome to implement a Lisp interpreter. These days nobody knows anymore what is Lisp. 
I will continue to fix the NN example and will proceed implementing built-ins

# Remarks

I was using https://rextester.com/l/common_lisp_online_compiler to test the real functionality.

ChatGPT is cool to generate TEST case (valid and invalid ones). PLEASE do not use it to generate the code - this you need to practice yourself

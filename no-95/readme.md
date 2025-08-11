# Overview

This repository is a FORTH interpreter written in Java. It is designed to be a simple and educational implementation of the FORTH programming language, showcasing its stack-based execution model and extensibility.

This challenge is based on the [Forth Interpreter Challenge](https://codingchallenges.substack.com/p/coding-challenge-95-forth-interpreter) by [Coding Challenges](https://codingchallenges.substack.com/).

# Features
- Stack-based execution model
- Support for defining new words (functions)
- Basic arithmetic operations
- Input parsing and execution of commands
- Extensible with new words
- Interactive REPL (Read-Eval-Print Loop)
- Error handling for invalid commands
- Support for comments in code
- Basic built-in commands
- Support for defining and using variables
- File execution with option '-f' for batch processing of commands
- Support for loading files with the `-l` option

# Usage

```bash
./forth.sh -h              
Usage: forth-repl [-hvV] [-vv] [-c=<command>] [-f=<file>] [-l=<fileToLoad>]
This challenge is to build your own Forth interpreter
  -c=<command>       command to execute - default: interactive mode
  -f=<file>          file to execute - default: interactive mode
  -h, --help         Show this help message and exit.
  -l=<fileToLoad>    file to load - default: none
  -v                 verbose model level 1
  -V, --version      Print version information and exit.
      -vv            verbose model level 2
```

# Building

Build using gradle:

```bash
./gradlew build installDist
```

# Run

To run the interpreter, you can use the provided shell script `forth.sh`. Make sure to give it execute permissions:

Then you can run it in interactive mode:


## Interactive Mode
```bash
./forth.sh
Welcome to the Forth interpreter!
ok> 
```

- the elements in the stack are displayed with
  <count> elem1 elem2 ...
- you switch it off / on with a flag: 

```forth
<1> 10 ok> false to echo-switch
ok> 1 7 4 10 20 * . cr
200
ok> true to echo-switch
<3> 1 7 4 ok> 
```

- `clear` to clean the whole stack
- `words` to show sorted list of words



## Command Line Mode
You can also execute commands directly from the command line:
```bash
./forth.sh -c "1 2 + . cr"
3
ok>
``` 


## File Execution
You can execute a file containing FORTH commands:   
```bash
./forth.sh -f script.forth
```

# Load files and run any
You can load a file with the `-l` option and then execute commands:
```bash
./forth.sh -l script.forth
ok> 1 2 + .
3 ok>
```

# Commands implemented

- Arithmetic operations: `+`, `-`, `*`, `/`, `mod`

```forth
ok> 10 20 +
<1> 30 ok> 
```

- Comparison: `=`, `<`, `>`, `<=`, `>=`, `<>`, `and`, `or`, `not`, `false`, `true`
  - boolean are represented as -1 = true. 0 = false

```forth
ok> 20 10 2 * = . cr
-1
ok> 
```


- Stack manipulation: `dup`, `drop`, `swap`, `over`, `2dup`, `2swap`
- Control flow: `if`, `else`, `then`
- Input/output: `.` (print top of stack) `cr` for new line
- Comments: `\ all cleaned until end` or `( all in parenthisid are ignored )`
- Defining new words: `: <word> <definition> ;`
- Loops: `do`, `loop`
- Stack inspection: `.s` (show stack)
- Clear stack: `clear`
- Variables: `variable <name>`, `@` (fetch), `!` (store)
- constants: `42 constant everything`. cannot be overwritten and writing content directly to stack `everything`
- flags / value: `true on echo-stack`. can be updated and written content to stack.  `echo-switch`
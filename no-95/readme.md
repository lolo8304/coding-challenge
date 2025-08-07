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
- Basic control flow with conditionals
- Support for defining and using variables
- File execution with option '-f' for batch processing of commands
- Load files with the `-l` option

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

## Command Line Mode
You can also execute commands directly from the command line:
```bash
./forth.sh -c "1 2 + ."
3 ok>
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
3 ok>```

# Commands implemented

- Arithmetic operations: `+`, `-`, `*`, `/`
- Stack manipulation: `dup`, `drop`, `swap`, `over`
- Control flow: `if`, `else`, `then`
- Input/output: `.` (print top of stack)
- Comments: `\` (everything after this on the line is ignored)
- Defining new words: `: <word> <definition> ;`
- Loops: 'do', 'loop'
- Variables: `variable <name>`, `@` (fetch), `!` (store)
- clear stack: `clear`
- show stack: .s

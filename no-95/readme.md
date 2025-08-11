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
- creating buffer, byte aligned, 32-bit aligned, static literals. with initialisation and printing

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

```bash
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


- Stack manipulation: `dup`, `drop`, `swap`, `over`, `2dup`, `2swap`, `2over`, `2drop`, `rot`, `-rot`, `clear`

    - `dup` ( n1 -- n1 n1 ) duplicates the top item on the stack.
    - `drop` ( n1 -- ) removes the top item from the stack.
    - `swap` ( n1 n2 -- n2 n1 ) swaps the top two items on the stack.
    - `over` ( n1 n2 -- n1 n2 n1 ) copies the second item on the stack to the top.
    - `2dup` ( n1 n2 -- n1 n2 n1 n2 ) duplicates the top two items on the stack.
    - `2swap` ( n1 n2 n3 n4 -- n3 n4 n1 n2 ) swaps the top two pairs of items on the stack
    - `2over` ( n1 n2 n3 n4 -- n1 n2 n3 n4 n1 n2 ) copies the second pair of items on the stack to the top.
    - `2drop` ( n1 n2 -- ) removes the top two items from the stack.
    - `rot` ( n1 n2 n3 -- n2 n3 n1 ) rotates the top three items on the stack.
    - `-rot` ( n1 n2 n3 -- n3 n1 n2 ) rotates the top three items on the stack in the opposite direction.


```forth
ok> 10 dup * . cr
100
ok> 10 20 swap
<2> 20 10 ok> 
```


- Control flow: `if`, `else`, `then`
  - `if` ( condition -- ) checks the top of the stack for a true value (-1) and executes the following code block if true.
  - `else` ( -- ) provides an alternative code block to execute if the condition is
  - `then` ( -- ) marks the end of the conditional block.

```forth
ok> 10 20 < if ." less" else ." greater" then cr
greater
ok> 10 20 > if ." less" else ." greater" then cr
less
ok>
```

- New words: `: <word> <definition> ;`

```forth
ok> : fuzz dup 3 mod 0 = if ." fuzz" drop else . then ;
ok> 9 fuzz
fuzz
ok> 10 fuzz
10
ok>
```


- Input/output: `.`, `cr`, `.s`, `emit`, `words`
  - `.` ( n -- ) prints the top item on the stack.
  - `cr` ( -- ) outputs a newline character.
  - `.s` ( -- ) displays the current stack contents.
  - `emit` ( c -- ) outputs a single character.
  - `words` ( -- ) lists all defined words in the interpreter.

```forth
ok> 10 . cr
10
ok> 65 emit cr
A
ok> 10 20 30 .s
<3> 10 20 30 ok>
ok> words
: ; .s 2dup 2drop 2over 2swap 2dup
2drop 2over 2swap dup drop swap over rot -rot clear if else then
.... etc
```

- Comments: `\ comment` or `( comment )`

```forth
ok> 10 \ this is a comment
ok> ( this is also a comment )
ok> 10 20 + ( inline comment add 2 ints ) . cr
```

- Loops: `do`, `loop`, `i`
  - `do` ( n1 n2 -- ) starts a loop from n1 to n2.
  - `loop` ( -- ) ends the loop.
  - `i` ( -- n ) retrieves the current loop index.

```forth
ok> 1 10 do i . loop cr
1 2 3 4 5 6 7 8 9 10
ok> 1 10 do i 2 * . loop cr
2 4 6 8 10 12 14 16 18 20
ok>
```


- Stack inspection: `.s` (show stack)
- Clear stack: `clear`
- Variables: `variable <name>`, `@` (fetch), `!` (store)
- constants: `42 constant everything`. cannot be overwritten and writing content directly to stack `everything`
- flags / value: `true on echo-stack`. can be updated and written content to stack.  `echo-switch`
- Create:
   - `create <name> <initial value>` creates a buffer with an initial value.
   - `@` fetches the value from the buffer.
   - `!` stores a value into the buffer.

```forth
ok> 42 constant everything
ok> everything .
42
ok> variable myVar
ok> 10 myVar !
ok> myVar @ .
10
```

- Create strings:

```forth
ok> create myString 5 C, char H C, char e C, char l C, char l C, char o C,
ok> myString count type cr
Hello
```
  myString is a 5+1 byte buffer, where the first byte is the count of characters, and the rest are the characters themselves. The `count` word retrieves the size of the string, and `type` prints it.
  [05] [H] [e] [l] [l] [o]


```forth
ok> create myString 5 C, ," Hello"
ok> myString count type cr
Hello
```
   create myString 5 C, ," Hello" is a shorthand to create a string with the given content.

```forth
CREATE msg S" Hello"  2DUP HERE SWAP MOVE  ALLOT
ok> msg 5 type cr
Hello
```
    This creates a string with the content "Hello" and allocates space for it. 
    The `2DUP HERE SWAP MOVE ALLOT` sequence is used to set up the string in memory.

```foth
oK> CREATE zstr  CHAR H C,  CHAR i C,  0 C,
ok> zstr ztype cr
Hi
ok> zstr zcount type cr
Hi
```
    This creates a zero-terminated string with the content "Hi". 
    The `ztype` word prints the string until it encounters a zero byte.
    The `zcount` word returns the size of the string, which is the number of characters before the zero byte.

- Miscellaneous:'

  - `invert` ( n -- n' ) inverts the bits of the top item on the stack.
  - `depth` ( -- n ) returns the number of items on the stack.
  - `clear` ( -- ) clears the entire stack.
  - `bye` ( -- ) exits the interpreter.
  - `.s` ( -- ) displays the current stack contents.
  - `.s?` ( -- ) displays the current stack contents with a count.
  - `allot` ( n -- ) allocates n bytes of memory.
  - `here` ( -- addr ) returns the current memory address.
  - `cells` ( -- n ) returns the size of a cell in bytes.
  - `cell+` ( addr -- addr' ) adds the size of a cell to the address.
  - `cell-` ( addr -- addr' ) subtracts the size of a cell from the address.
  - `c@` ( addr -- c ) fetches a byte from memory.
  - `c!` ( c addr -- ) stores a byte into memory.
  - `!` ( n addr -- ) stores a value into memory.
  - `+!` ( n addr -- ) adds a value to the memory location.
  - `@` ( addr -- n ) fetches a value from memory.
  - `move` ( src dst n -- ) copies n bytes from src to dst.
  - `type` ( addr n -- ) prints n bytes from memory starting at addr.
  - `ztype` ( addr n -- ) prints n bytes from memory starting at addr
  - `nip` ( n1 n2 -- n2 ) removes the top item from the stack and returns the second item.
  - `tuck` ( n1 n2 -- n2 n1 n2 ) duplicates the second item and places it on top of the stack.
  - `words` ( -- ) lists all defined words in the interpreter.
  - `key` ( -- c ) reads a single character from input.
  - `false` ( -- ) pushes a false value (0) onto the stack.
  - `true` ( -- ) pushes a true value (-1) onto the stack.
  - `c,` ( c -- ) stores a character into memory.
  - `count` ( addr -- n ) returns the size of a string in bytes.

# Example Scripts for all words

```forth
\ ! ( x addr -- ) store a value into a memory cell
variable foo  \ allocate a cell
42 foo ! foo @ . cr

\ * ( n1 n2 -- n3 ) multiply
6 7 * . cr

\ + ( n1 n2 -- n3 ) add
10 20 + . cr

\ +! ( n addr -- ) add n to the value stored at addr
variable counter  5 counter !
3 counter +! counter @ . cr

\ - ( n1 n2 -- n3 ) subtract
50 8 - . cr

\ -rot ( x1 x2 x3 -- x3 x1 x2 ) rotate the third to top
1 2 3 -rot .s cr  \ expect: <3> 3 1 2

\ . ( x -- ) print top of stack
123 . cr

\ .s ( -- ) show stack contents (non-standard but common)
1 2 3 .s cr

\ .s? ( -- ) print stack only if bon empty
.s? . cr

\ / ( n1 n2 -- q ) integer division
84 2 / . cr

\ 2drop ( x1 x2 -- ) drop two values
10 20 2drop .s cr

\ 2dup ( x1 x2 -- x1 x2 x1 x2 ) duplicate a pair
1 2 2dup .s cr

\ 2over ( x1 x2 x3 x4 -- x1 x2 x3 x4 x1 x2 ) copy two from below
10 20 30 40 2over .s cr

\ 2swap ( x1 x2 x3 x4 -- x3 x4 x1 x2 ) swap two pairs
1 2 3 4 2swap .s cr

\ < ( n1 n2 -- flag ) less-than
5 10 < . cr

\ <= ( n1 n2 -- flag ) less-or-equal
5 5 <= . cr

\ <> ( n1 n2 -- flag ) not-equal
4 5 <> . cr

\ = ( n1 n2 -- flag ) equal
42 42 = . cr

\ > ( n1 n2 -- flag ) greater-than
10 5 > . cr

\ >= ( n1 n2 -- flag ) greater-or-equal
10 10 >= . cr

\ @ ( addr -- x ) fetch from memory cell
variable bar  99 bar !  bar @ . cr

\ allot ( n -- ) reserve n bytes in the dictionary
here 10 allot here swap - . cr  \ prints 10

\ and ( x1 x2 -- x3 ) bitwise AND
6 3 and . cr  \ 6=110,3=011 -> 2

\ bye ( -- ) exit interpreter (left commented so script continues)
\ bye

\ c! ( char addr -- ) store a byte
create cbuf 1 allot  65 cbuf c!  cbuf c@ . cr

\ c, ( char -- ) compile a byte into the dictionary
here 66 c,  here 1- c@ . cr

\ c@ ( addr -- char ) fetch a byte
create cbuf2 1 allot  90 cbuf2 c!  cbuf2 c@ . cr

\ cell+ ( addr -- addr' ) advance by one cell
foo cell+ foo - . cr  \ prints cell size in bytes

\ cell- ( addr -- addr' ) move back by one cell
foo cell+ cell- foo - . cr  \ prints 0

\ cells ( n -- n*cell-size ) scale by cell size
1 cells . cr  \ prints cell size in bytes

\ clear ( -- ) clear the data stack (implementation-specific)
1 2 3 clear .s cr

\ count ( c-addr -- c-addr u ) from counted string
s" Hello" count type cr

\ cr ( -- ) newline
." Line 1" cr ." Line 2" cr

\ depth ( -- n ) number of items on the stack
1 2 3 depth . cr

\ drop ( x -- ) drop top of stack
42 drop .s cr

\ dup ( x -- x x ) duplicate top of stack
5 dup .s cr

\ echo-stack ( -- bool ) sets flag true / false if stack agould be displayed at every command. default false (implemntation specific)
true to echo-stack \ switch on
false to echo-stack \ switch off

\ emit ( char -- ) print a single character
65 emit cr  \ prints: A

\ false ( -- 0 ) boolean false
false . cr

\ here ( -- addr ) current dictionary pointer
here . cr

\ invert ( x -- ~x ) bitwise NOT
0 invert . cr  \ typically -1

\ key ( -- char ) read a key (commented to avoid blocking)
\ ." Press a key: " key . cr

\ mod ( n1 n2 -- r ) remainder
17 5 mod . cr

\ move ( addr1 addr2 u -- ) move u bytes (overlapping-safe)
create src 5 allot
create dst 5 allot
s" Hello" src swap move
src dst 5 move
dst 5 type cr

\ nip ( x1 x2 -- x2 ) drop the next-to-top item
1 2 nip . cr

\ not ( flag -- flag' ) boolean NOT (implementation-specific)
true not . cr

\ or ( x1 x2 -- x3 ) bitwise OR
2 4 or . cr

\ over ( x1 x2 -- x1 x2 x1 ) copy second item
1 2 over .s cr

\ rot ( x1 x2 x3 -- x2 x3 x1 ) rotate top three
1 2 3 rot .s cr

\ swap ( x1 x2 -- x2 x1 ) swap top two
10 20 swap .s cr

\ true ( -- -1 ) boolean true
true . cr

\ tuck ( x1 x2 -- x2 x1 x2 ) tuck copy beneath
1 2 tuck .s cr

\ type ( c-addr u -- ) print a string
s" Hi there" type cr

\ words ( -- ) list dictionary words (may print a lot)
words

\ zcount ( z-addr -- c-addr u ) get addr/len of C-string
create msg z" Hello"
msg zcount type cr

\ ztype ( z-addr -- ) print a zero-terminated string
create greet z" Hello"
greet ztype cr

```

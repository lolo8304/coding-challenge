# Challenge 34 - Write Your Own jq

This is the challenge 34 from the Coding Challenges by John Crickett https://codingchallenges.fyi/challenges/challenge-jq

## Description

This solution implements a jq (json query) evaluator

- its based on my own made jquery parser and object model from challenge-no https://github.com/lolo8304/coding-challenge/tree/main/no-2

- a new lexer + parser again for the jq language
- build a simple node tree to use for recursive evaluation of jq expressions
- implement own classes per expression type
- built a new JsonSerializer with compact, indentations etc in it, to be able to pretty-print the json


### Final result

the final array builder uses all features (. | [] selectors)

```bash
curl -s 'https://api.github.com/repos/CodingChallegesFYI/SharedSolutions/commits?per_page=3' | ./jq.sh "[.[].[\"commit\"].author | { name, email }] "
[
    {
        "name": "Lorenz aka Lolo Hänggi",
        "email": "lolo8304@gmail.com"
    },
    {
        "name": "Ethan Costa",
        "email": "88796846+dethancosta@users.noreply.github.com"
    },
    {
        "name": "Carlos Gómez",
        "email": "60867448+carlex05@users.noreply.github.com"
    }
]

```


There is a very good documention about the language features. https://jqlang.github.io/jq/manual/
Could be lots of additional challenges to be solved :-)

## Build

Open Intellij or any other IDE with gradle support. run "installDist"

## Tests

Run test cases using gradle "test" command. checkout many test files for bit converter, qr code, qr mode, regions, version

## Usage

### Commandline parameters

I am using picocli to parse arguments

```bash
./jq.sh -h
Usage: jq [-hvV] [-vv] [<jqFilter>] [<files>...]
This challenge is to build your own jq - see https://codingchallenges.
fyi/challenges/challenge-jq
      [<jqFilter>]   jq filter expressions - default '.'
      [<files>...]   files - optional
  -h, --help         Show this help message and exit.
  -v                 -v specifies verbose level
  -V, --version      Print version information and exit.
      -vv            -vv specifies verbose level 2
```

remark: the coding was quite simple, there is was no need for -v or -vv

### Examples: 

get all quotes of objects. checkout the real json answer via https://dummyjson.com/quotes?limit=2
```bash
curl -s 'https://dummyjson.com/quotes?limit=2' | ./jq.sh '.["quotes"]'
[
    {
        "id": 1,
        "quote": "Life isn’t about getting and having, it’s about giving and being.",
        "author": "Kevin Kruse"
    },
    {
        "id": 2,
        "quote": "Whatever the mind of man can conceive and believe, it can achieve.",
        "author": "Napoleon Hill"
    }
]
```


search for the first element, and the the commit message
```bash
curl -s 'https://api.github.com/repos/CodingChallegesFYI/SharedSolutions/commits?per_page=3' | ./jq.sh '.[0] | .commit.message'
"Added QR code generator  (#72)\n\n* Create challenge-qr-generator.md\r\n\r\n* Update README.md with QR code generator"
```


### Debugging:

- I prepared -v and -vv, but I did not use it

## TODO / Bugs

- some of the code in the evaluator could be refactored heavily
- push back the json parser code to the example no-2 code

## Final note

I did not know "jq" before this challenge. It shows that John's challenges are always good to learn coding something new and also get to know something new.


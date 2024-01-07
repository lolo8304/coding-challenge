
# Coding challenge 1 - word counter

from
https://codingchallenges.fyi/challenges/challenge-wc/
by
https://twitter.com/johncrickett

Since a few years I am in C# but I wanted to try out java for this challenge.

# Technologies and Libraries

package manager: gradle

I am using following libraries as support

- command line tool:
  - info.picocli:picocli
  - https://picocli.info/


# Install and run

## Build

I use gradle version 7.3 and this is related to java 17 using OpenJDK

- checkout comp matrix https://docs.gradle.org/current/userguide/compatibility.html

you need to start folder "no-1" as your main folder (not in the parent folder)

```bash
./gradlew clean installDist
```


## Run

without any parameter you will see the help information

```bash
./wc.sh
Missing required parameter: '<file>'
Usage: ccwc [-chlVw] <file>
counts the number for lines, words, characters
<file>      The file to calculate for.
-c              -c for counting characters
-h, --help      Show this help message and exit.
-l              -l for counting lines
-V, --version   Print version information and exit.
-w              -l for counting words
```

## Run on a test file

Open 3 terminals and start in each

```bash 
./wc.sh app/src/test/resources/text.txt
  7137 58159 341836 text.txt
```

line count
```bash 
./wc.sh -l app/src/test/resources/text.txt
  7137 text.txt
```



# Final remark

Thanks https://twitter.com/johncrickett for the great challenge

I fully recommend this challenges for all Software Engineers who would like get better
https://codingchallenges.fyi/challenges/challenge-load-balancer/

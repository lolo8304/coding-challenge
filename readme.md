# Coding challenges

These great challenges are published at https://codingchallenges.fyi by John Cricket [Twitter](https://twitter.com/johncrickett)

Checkout the newsletter also here on [Substack](https://codingchallenges.fyi/)

I have started on week 1 in the implementing the challenges starting march 2023. Since then John published every week a new challenge.

I love the most the real world applications as Redis, load balancer, webserver, IRC client, json parse, url shortener and more to come ....

I started to use Discort and [Twitter](https://twitter.com/Lolo46822032) to share some solutions.

I decided to implement it back with Java. I have been working with Java since v1.0 back in 2000 and worked with it professionally for more than 18 years. Then I started with C# and powershell working with Azure Cloud.

# Write your own

here you can find all my implementation

- 1 wc tool - word counter
  - https://github.com/lolo8304/coding-challenge/tree/main/no-1
  - very simple tool to get back with Java tooling, pipelines and arg parsing
- 2 JSON parser
  - https://github.com/lolo8304/coding-challenge/tree/main/no-2
  - this is a great project to implement scanner and parser and tried to strong test it with lots of test cases (pos and neg cases)
  - if you need testcases - check here https://github.com/lolo8304/coding-challenge/tree/main/no-2/app/src/test/resources/tests
- 3 Compression tool
  - https://github.com/lolo8304/coding-challenge/tree/main/no-3
  - get into the Huffman Encoder/Decoder - quite complex binary trees with insertion logic to implement
  - without testcases - no chance to solve this riddle
  - awesome experience John
- 4 cut Tool
  - https://github.com/lolo8304/coding-challenge/tree/main/no-4
  - little linux tool. simple and easy
- 5 Load Balancer
  - https://github.com/lolo8304/coding-challenge/tree/main/no-5
  - documented with readme.md
  - invested quite a bit of time for fully dynamic and reliable Round Robin
  - and chaos engineering tests to kill BE servers and not to loose any message
- 6 sort Tool: not implemented yet
- 7 Calculator: not implemented yet
- 8 Redis Server
  - https://github.com/lolo8304/coding-challenge/tree/main/no-8
  - this is by far the best challenge I have every implemented
  - learning the redis protocol step by step and implementing a fully functional server to use redis-benchmark was great
  - I extended the challenge with lots of commands incl. docu and meta data
  - this is AWESOME idea by John to implement
  - with this you can really learn a new language
  - dont look at performance - more to code redactorings and testcases
  - I implemented a lot of test cases: I used chatGPT to generate test cases for me
  - https://github.com/lolo8304/coding-challenge/tree/main/no-8/app/src/test/resources
- 9 grep Tool
  - https://github.com/lolo8304/coding-challenge-9
  - I used just my mobile phone and javascript to implement it
  - has some headache topics with \* and folders .... check it out
- 10 uniq Tool: not implemented yet
- 11 WebServer
  - https://github.com/lolo8304/coding-challenge/tree/main/no-11
  - cool to learn the HTTP1.1 protocol and implement it without support libraries
  - I extended my work to implement Servlet types (static files, rest) with routing
  - at the end I realized that maybe the scanner is a bit an overkill
  - but funny to solve
- 12 UrlShortener: not implement, we implemented this just some months ago in my company :-)
- 13 diff Tool: not implemented yet
- 14 shell: not implemented yet
- 15 cat Tool: not implemented yet
- 16 IRC Client:
  - https://github.com/lolo8304/coding-challenge/tree/main/no-16
  - great to learn the protocol of IRC from the client perspective
  - massive refactoring from step 2 to step 3 - as supposed
  - I tried to this not to build a scanner but use regexp for the different patterns. finally I must say - I like scanners.
  - without https://regex101.com/ - no chance to implement this type of regexp
- 17 memcached server:
  - https://github.com/lolo8304/coding-challenge/tree/main/no-17
  - fully functional server with many commands, memory mgmt
  - cmd pattern to plugin mire if interested
  - lots of testcases
  - additional memcache client test
  - implement a server and a client.
  - Later: try to use consistent hashing for multiple servers.
- 18 spotify client: not implemented yet
- 19 discort bot:
  - https://github.com/lolo8304/coding-challenge/tree/main/no-19
  - configured my own discort server
  - implemented a pluggable cmds
  - not deloyed on a server
- 20 LinkedIn carousel generator: not implemented
- 21 sed-tool: not implemented yet
- 22 DNS resolver:
  - https://github.com/lolo8304/coding-challenge/tree/main/no-22
  - implemented A, cname, mx, txt records
  - resolver does same printout as 'nslookup'
  - can be parametrized to test dns principles
  - default using localhost loopback 127.0.0.53:53
- 23 traceroue: not implemented yet
- 24 realtime chat client and server: not implemented yet
- 25 NATS server
  - https://github.com/lolo8304/coding-challenge/tree/main/no-25
  - implemented NATS server and client
  - cool challenge
- 26 git client: not implemented yet
- 27 rate limiter: not implemented yet
- 28 ntp client: not implemented yet
- 29 scheduling automation app: not implemented yet
- 30 Lisp interpreter
  - https://github.com/lolo8304/coding-challenge/tree/main/no-30
  - implemented full Lisp tokenizer, parser and interpreter.
  - implemented > 30 built-ins incl pure function caching for faster execution
  - love Lisp. used it to build first neuronal network with 10 own perceptron (back in 1994)
  - additional idea: build interpreter to build this again
- 31 QR Code Generator
  - https://github.com/lolo8304/coding-challenge/tree/main/no-31
- 32 Crontab: not implemented yet


# A brief history of my coding languages learned and used

- Back in 1994 I learned to code in [BASIC](https://en.wikipedia.org/wiki/BASIC) on [Sharp 1401](https://en.wikipedia.org/wiki/Sharp_PC-1401) and with MS-DOS in 1985 on my 1st PC 4.77 Mhz, with a Hercules monochrome grafic card, 1MB RAM, 20MB Disk :-) and Floppies. And sure got to know [Assembly](https://en.wikipedia.org/wiki/Assembly_language#Assembler) to do all crazy stuff down the low level.
- With the [Apple Macintosh 512e](https://en.wikipedia.org/wiki/Macintosh_512Ke) used in highschool I started coding with [Pascal](https://en.wikipedia.org/wiki/Apple_Pascal) by [Niklaus Wirth](https://people.inf.ethz.ch/wirth). During an internship I used Pascal to run on an [VAX by DEC](https://en.wikipedia.org/wiki/VAX)
- [Modula-2](https://en.wikipedia.org/wiki/Modula-2) was the successor of Pascal also by [Niklaus Wirth](https://people.inf.ethz.ch/wirth) and here I started all my private coding
- At University ETH Zurich I used to code in [Oberon](<https://en.wikipedia.org/wiki/Oberon_(programming_language)>) by [Niklaus Wirth](https://people.inf.ethz.ch/wirth) and [Oberon-2](https://en.wikipedia.org/wiki/Oberon-2) the object oriented extension of Oberon. I loved it.
- Beside Oberon we use to code our first Neuronal network with 10 neurons in [Lisp](https://de.wikipedia.org/wiki/Lisp) and our first knowledge base in [Prolog](https://en.wikipedia.org/wiki/Prolog) - something completly different but nice.
- At my first business job in 1994 I learned and loved [Smalltalk](https://people.inf.ethz.ch/wirth/Oberon/index.html) - the real object oriented language. In a private project, I even implemented my own Smalltalk parser, compiler and byte code runtime environment in Oberon based on the architecture and bytecode from [A little Smalltalk](https://rmod-files.lille.inria.fr/FreeBooks/LittleSmalltalk/ALittleSmalltalk.pdf). I also used [Visual-Age Smalltalk](https://en.wikipedia.org/wiki/VisualAge) in my STA in Dallas, TX, USA in 1999.
- In 2000 I arrived at the WWW finally and I learned Java and [JDK 1.x](https://en.wikipedia.org/wiki/Java_version_history) and used Java for more then 18 years in business and private.
- During Hackathons I learned to code in [Javascript](https://en.wikipedia.org/wiki/JavaScript), [node.js](https://en.wikipedia.org/wiki/Node.js), [Solidy](https://en.wikipedia.org/wiki/Solidity#:~:text=Solidity%20is%20the%20primary%20language,enterprise%2Doriented%20Hyperledger%20Fabric%20blockchain.)
- with winning an innovation award in 2014 I started coding with Apple Objective-c for iPhone and Apple Watch Series 1 and later with Swift and SwiftUI for our winning Hackathon idea [m-clippy](https://devpost.com/software/m-clippy).
- Coding for my beloved Hololens 1 developer edition I started with Unity and C#. Including Hackathons at Microsoft in Redmond with Principal engineers from the Hololens team. Implement 1st holographic bot called [LoloBot](https://github.com/lolo8304/LoloBot).
- Then I arrived in the Azure Cloud and used [C#](<https://en.wikipedia.org/wiki/C_Sharp_(programming_language)>) and [powershell](https://en.wikipedia.org/wiki/PowerShell) for more than 3 years now.
- Now back at OpenAPIs development with Nodejs (nestjs) with typescript 

Now in 2023 I thought coming back to Java for these challenges. Unfortunatly Oberon and Smalltalk are almost dead.




I wanted to thank John Cricket for these challenges and I encourage every Engineer from Junior to Principal to do this. Learn a new language or get back to language you have used to code (as me here in Java).

And also learn to understand real-world applications while writing your own version of it.

thx
Lolo

[LinkedIn](linkedin.com/in/lorenzhaenggi)
[Twitter](https://twitter.com/Lolo46822032)

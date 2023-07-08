# Coding challenges 

These great challenges are published at https://codingchallenges.fyi by John Cricket [Twitter](https://twitter.com/johncrickett)

Checkout the newsletter also here on [Substack](https://codingchallenges.fyi/)

I have started on week 1 in the implementing the challenges starting march 2023. Since then John published every week a new challenge.

I love the most the real world applications as Redis, load balancer, webserver, IRC client, json parse, url shortener and more to come ....

I started to use Discort and [Twitter](https://twitter.com/Lolo46822032) to share some solutions.

I decided to implemented back with Java. I have been working with Java since 1.0 back in 2000 and worked with it professionally for more than 18 years. Then I started with C# and powershell working with Azure Cloud.
Now 2023 I tought coming to learn back Java using these challenges.

# Write your own

here you can find all my implementation

- 1 wc tool
    - word counter https://github.com/lolo8304/coding-challenge/tree/main/no-1
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
    - https://github.com/lolo8304/coding-challenge/tree/main/no-4v
    - little linux tool. simple and easy
- 5 Load Balancer
    - https://codingchallenges.fyi/challenges/challenge-load-balancer
    - documented with readme.md
    - invested quite a bit of time for fully dynamic and reliable Round Robin
    - and chaos engineering tests to kill BE servers and not to loose any message
- 6 Sort: not implemented
- 7 Calculator: none implemented
- 8 Redis Server
    - https://codingchallenges.fyi/challenges/challenge-redis
    - this is by far the best challenge I have every implemented
    - learning the redis protocol step by step and implementing a fully functional server to use redis-benchmark was great
    - I extended the challenge with lots of commands incl. docu and meta data
    - this is AWESOME idea by John to implement
    - with this you can really learn a new language
    - dont look at performance - more to code redactorings and testcases
    - I implemented a lot of test cases: I used chatGPT to generate test cases for me
    - https://github.com/lolo8304/coding-challenge/tree/main/no-8/app/src/test/resources
- 9 grep
    - https://github.com/lolo8304/coding-challenge-9
    - I used just my mobile phone and javascript to implement it
    - has some headache topics with * and folders .... check it out
- 10 uniq Tool: not implemented
- 11 WebServer
    - https://github.com/lolo8304/coding-challenge/tree/main/no-11
    - cool to learn the HTTP1.1 protocol and implement it without support libraries
    - I extended my work to implement Servlet types (static files, rest) with routing
    - at the end I realized that maybe the scanner is a bit an overkill
    - but funny to solve
- 12 UrlShortener: not implement, we implemented this just some months ago in my company :-)
- 13 diff Tool: not implemented
- 14 shell: not implemented
- 15 cat Tool: not implemented
- 16 IRC Client:
    - https://github.com/lolo8304/coding-challenge/tree/main/no-16
    - great to learn the protocol of IRC from the client perspective
    - massive refactoring from step 2 to step 3 - as supposed
    - I tried to this not to build a scanner but use regexp for the different patterns. finally I must say - I like scanners. 
    - without https://regex101.com/ - no chance to implement this type of regexp

I wanted to thank John Cricket for this challenges and I encourage every Engineer from Junior to Principal to do this. Learn a new language or get back to language you have used to code (as me here in Java). 

And also learn to understand real-world applications while writing your own version of it. 

thx
Lolo

[LinkedIn](linkedin.com/in/lorenzhaenggi)
[Twitter](https://twitter.com/Lolo46822032)
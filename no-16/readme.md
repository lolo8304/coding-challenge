# n-16 Build you own IRC client

the challenge is coming from https://codingchallenges.fyi/challenges/challenge-irc/
by John Cricket

## Step Zero

I use visual code with the java extension and gradle as build tool


## Step 1

To start I created a main class Irc with parameters

- -s: irc server = default irc.freenode.net
- -p: port = default 6667
- -n: nick name = default CClolo8304
- -c: channel = default CC

I use https://picocli.info/ as everywhere in my challenges to parse arguments

## Step 2

implement proper ping / pong messages to stay connected and check what's coming back from the server

implement your first simple response parsing - and refactor later. Check out the documentation https://datatracker.ietf.org/doc/html/rfc2812 for all type and syntax of commands as 372 MOTD - message of the day.

## Step 3
Heavy refactoring and introduce now testcases for all types of responses. Check how to refactor to be able to test in a simple way.

https://github.com/lolo8304/coding-challenge/tree/main/no-16/app/src/test/resources/tests

Try to implement beside positive testcases also negative tests. Here you will figure out that the IRC protocol is not all the time implemented correctly. Example: token of the ping should be only A-Za-z0-9_- but some servers use the whole ascii alphabet.

I also introduced 2 Interfaces for send messages and receiving messages for a better and more understandable code. And finally I used IrcMessage as a temporary object after parsing to make business logic of commands easier.

## Step 4 5 6
Implement nice responses to the client based on some special commands as NICK, PRIVMSG, JOIN, PART, QUIT

implement your own command handler while writing to console based on the specification

https://github.com/lolo8304/coding-challenge/blob/main/no-16/app/src/main/java/irc/message/IrcGeneralMessage.java

example
```java
    @Override
    public void handle(IIrcSenderProtocol sender) throws IOException {
        switch (this.command) {
            case NICK:
                sender.printMessage(String.format("%s is now known as %s", this.sender, this.content));
                break;
            case JOIN:
                sender.printMessage(String.format("%s has joined %s", this.sender, this.content));
                break;
            case QUIT:
                sender.printMessage(String.format("%s has left IRC %s", this.sender, this.content));
                break;

            case MOTD_START:
                break;
            case MOTD_END:
                break;
            case MOTD:
            case WELCOME:
            case CREATED:
            case YOUR_HOST:
                sender.printMessage(String.format("%s", this.content));
                break;

            default:
                sender.printMessage(String.format("Cmd '%s' missing: %s", this.command, this.content));
                break;
        }
    }
```

## missing

I have only implemented an IrcClient and not a rest API to be able to be served by a webclient. 

I also did not implement a little shell to interact with the server. This can be done easily later
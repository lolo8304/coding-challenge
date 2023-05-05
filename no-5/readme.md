# Coding challenge - Load balancer

from
https://codingchallenges.fyi/challenges/challenge-load-balancer/
by
https://twitter.com/johncrickett

Since a few years I am in C# but I wanted to try out java for this challenge.

# Technologies and Libraries

package manager: gradle

I am using following libraries as support
- command line tool:
    - info.picocli:picocli
    - https://picocli.info/
- webserver:
    - org.eclipse.jetty:jetty-server 
    - https://www.eclipse.org/jetty/
- http client:
    - com.squareup.okhttp3
    - https://square.github.io/okhttp/

shell scripts to start backends and load balancer.

I have also implemented a chaos engineering to kill some backends and start them again

# Steps by challeng

## Step 0

I am using Visual Code with some plugins for java, gradle, linting, testing
I am using github as repo

## Step 1: basic webserver

I have choosen jetty because it is simple, small and easy to create 1 or 2 APIs. enough for this challenge.
with the use of picocli it is very simple to implement a little command line tool. I decided to use the same main class for all backends and loadbalancer

starting a backend on port 9000
```
app -b -p=9000
```

starting a load balancer on port 8080
```
app -p=8080
```

later I have refactored it to use additional parameters for a backend list 

starting a load balancer with a backend list
```
app -p=8080 -blist="http://localhost:9000,http://localhost:9000"
```

### Implementation details

design used 2 Main classes SimpleBackend and SimpleLoadBalancer instantiated via delegation from the main class.

```java
protected SimpleBackend(int port) 
protected SimpleLoadBalancer(int port)
```

later I have introduced an additional parameter for the Loadbalancer strategy - defaulted to RoundRobin
```java
protected SimpleLoadBalancer(int port, LoadBalancerStrategy lbStrategy)
```

to server many requests in parallel I have already added a server with thread pool - here with 20

```java
var server = new Server(new QueuedThreadPool(20));
```
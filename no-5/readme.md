# Coding challenge 5 - Load balancer

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

# Steps by challenge

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

For the implementation of the load balancer I am using OkHttp3 library. I tried many others but they had been intering with jetty webserver.

here is the refactored method to execute a request to a backend server url

```java
    private Response executeRequest(String beUrl, HttpServletRequest request) throws IOException {
        var httpClient = new OkHttpClient();
        var reqBuilder = new Request.Builder()
                .url(beUrl + request.getRequestURI());
        var reqHeaders = request.getHeaderNames();
        while (reqHeaders.hasMoreElements()) {
            var reqHeaderKey = reqHeaders.nextElement();
            var reqHeaderValue = request.getHeader(reqHeaderKey);
            reqBuilder.header(reqHeaderKey, reqHeaderValue);
        }
        var beRequest = reqBuilder.build();
        return httpClient.newCall(beRequest).execute();
}
```
I also dynamically copied all the headers from the request and sent it to the backend. For simplicity i implemented a simple get and no body is taken over. This could be done on the beRequest builder. 
```java
        var beRequest = reqBuilder.build();
```

## Step 2: load balancer strategy RoundRobin

as described there are many Load balancer strategies. Here I am implementing only RoundRobin but used a Superclass to be able to implement later somemore else.

as described in Step 1 I refactored now the main classes to pass multiple servers.
To be able to simulate a dynamic list of backends, I implemented some bash shell scripts to launch and kill servers.


### Simple start backend and loadbalancer
start an own backend server with a port. here you can see the logs in the console
```bash
./be.sh 9000

INFO: Start Backend on port 9000
[main] INFO org.eclipse.jetty.server.Server - jetty-11.0.15; built: 2023-04-11T18:37:53.775Z; git: 5bc5e562c8d05c5862505aebe5cf83a61bdbcb96; jvm 11.0.9+7-LTS
[main] INFO org.eclipse.jetty.server.session.DefaultSessionIdManager - Session workerName=node0
[main] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.ServletContextHandler@793f29ff{/,null,AVAILABLE}
[main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@4facf68f{HTTP/1.1, (http/1.1)}{0.0.0.0:9000}
[main] INFO org.eclipse.jetty.server.Server - Started Server@3c0be339{STARTING}[11.0.15,sto=0] @524ms
```

start an the load balancer with an example of 2 backends. Here you can see the logs in the console
```bash
./lb.sh 

May 06, 2023 12:02:17 AM lb.SimpleLoadBalancer <init>
INFO: Start Loadbalancer on port 8080
[main] INFO org.eclipse.jetty.server.Server - jetty-11.0.15; built: 2023-04-11T18:37:53.775Z; git: 5bc5e562c8d05c5862505aebe5cf83a61bdbcb96; jvm 11.0.9+7-LTS
[main] INFO org.eclipse.jetty.server.session.DefaultSessionIdManager - Session workerName=node0
[main] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.ServletContextHandler@32502377{/,null,AVAILABLE}
[main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@50eac852{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
[main] INFO org.eclipse.jetty.server.Server - Started Server@548e6d58{STARTING}[11.0.15,sto=0] @541ms
```

### Start a dynamic list of backends

I have implemented a dynamic script to start backend and load balancer and also kill the processes before to have a fast restart.

here you can start all and logs are written to ./logs/ folder for tailing
```bash
./be-servers.sh 

all killed
start all backends
start load balancer
```
you can adapt the number of backends available in the script
```bash
N=20
```

additionally you can also kill all or just start all

just kill all
```bash
./be-servers.sh -kill
all killed
```

just start all backends, no load balancer and no kill
```bash
./be-servers.sh -nokill
start all backends
```

### Test running requests

a simple while loop is firing at a high speed in parallel and with a sleep of 0.5. this can be adapted in the script
```bash
./test.sh
from http://localhost:9007 // Replied with a hello message

from http://localhost:9004 // Replied with a hello message

from http://localhost:9003 // Replied with a hello message

from http://localhost:9001 // Replied with a hello message

from http://localhost:9005 // Replied with a hello message
```

THe load balancer is returning back the backend and its message for testing. For sure this is not a real load balancer, but good for debugging and showcasing.


### Chaos engineering - the fun part

Now it gets interesting and funny. I wanted to see that I dont loose any message to the load balancer even if lots of backend servers are killed suddenly

some additional remarks
- a health check is running every 2s for all backends
- a simple retry mechanism if the backend was chosen by RoundRobin but suddenly killed
- if a retry fails the backend server is move to **unhealthy**

you can now start killing N backend servers

```bash
./chaos.sh 15
kill now 33283
kill now 33285
kill now 33276
...
kill now 33272
start all backends
```

this will kill randomly 15 backends, sleep 2s in between and then call the be-servers.sh -nokill to start the again.

now you should see if you loose one of your client requests. I hope not.

### Logging

I use very restricted logging to not flood the logs. Most importent events are written out

every backend and the load balance has own logs
```bash
-rw-r--r--   1 Lolo  staff  14780 May  6 00:18 be-9000.log
-rw-r--r--   1 Lolo  staff  36722 May  6 00:18 be-9001.log
-rw-r--r--   1 Lolo  staff  36601 May  6 00:18 be-9002.log
-rw-r--r--   1 Lolo  staff  36722 May  6 00:18 be-9003.log
...
-rw-r--r--   1 Lolo  staff  13929 May  6 00:18 be-9017.log
-rw-r--r--   1 Lolo  staff  13929 May  6 00:18 be-9018.log
-rw-r--r--   1 Lolo  staff  36601 May  6 00:18 be-9019.log
-rw-r--r--   1 Lolo  staff  23857 May  6 00:18 lb.log
```

- if a backend server is added based on the Healthcheck
- if a backend server is removed based on the negative Healthcheck or after a retry
- successful or failure access log for every request to the load balancer

if you run the chaos.sh script 
```bash
./chaos.sh 5
kill now 33839
kill now 33847
kill now 33836
kill now 33850
kill now 33281
start all backends
```
you can see the following events



```bash
tail -f lb.log

May 06, 2023 12:18:19 AM lb.SimpleLoadBalancer log
INFO: http://localhost:9019 [0:0:0:0:0:0:0:1] GET /hello HTTP/1.1 User-Agent: Wget/1.20.3 (darwin20.1.0)
May 06, 2023 12:20:15 AM lb.SimpleLoadBalancer log
INFO: http://localhost:9011 [0:0:0:0:0:0:0:1] GET /hello HTTP/1.1 User-Agent: Wget/1.20.3 (darwin20.1.0)
May 06, 2023 12:20:15 AM lb.SimpleLoadBalancer log
INFO: http://localhost:9005 [0:0:0:0:0:0:0:1] GET /hello HTTP/1.1 User-Agent: Wget/1.20.3 (darwin20.1.0)
May 06, 2023 12:20:16 AM lb.SimpleLoadBalancer log

...
May 06, 2023 12:20:02 AM lb.strategies.LoadBalancerStrategy unhealthy
INFO: - Unhealthy backend: http://localhost:9007
May 06, 2023 12:20:02 AM lb.SimpleLoadBalancer log
INFO: http://localhost:9015 [0:0:0:0:0:0:0:1] GET /hello HTTP/1.1 User-Agent: Wget/1.20.3 (darwin20.1.0)
May 06, 2023 12:20:03 AM lb.strategies.LoadBalancerStrategy unhealthy
INFO: - Unhealthy backend: http://localhost:9015
...

May 06, 2023 12:20:16 AM lb.strategies.LoadBalancerStrategy healthy
INFO: + Healthy backend: http://localhost:9007
May 06, 2023 12:20:16 AM lb.strategies.LoadBalancerStrategy healthy
INFO: + Healthy backend: http://localhost:9004
May 06, 2023 12:20:16 AM lb.strategies.LoadBalancerStrategy healthy
INFO: + Healthy backend: http://localhost:9013

```


details - remove unhealthy
```bash
May 06, 2023 12:20:03 AM lb.strategies.LoadBalancerStrategy unhealthy
INFO: - Unhealthy backend: http://localhost:9015
```

details - add healthy back
```bash
May 06, 2023 12:20:16 AM lb.strategies.LoadBalancerStrategy healthy
INFO: + Healthy backend: http://localhost:9007
```

details - normal access log
```bash
May 06, 2023 12:18:19 AM lb.SimpleLoadBalancer log
INFO: http://localhost:9019 [0:0:0:0:0:0:0:1] GET /hello HTTP/1.1 User-Agent: Wget/1.20.3 (darwin20.1.0)
```

## Step 3 and beyond

all the details used to validate and test RoundRobin are already documented in Step 2

### Health check implementation

important of a healthcheck is
- should be executed very fast
- should not get blocked or timeout to just say 'it not running'
- fail if layer 4 is not working and then run the /health api
- implement client side with connection and read timeouts

I added a TCP socket verification
```java
    private boolean isTcpHealthy(String urlString) {
        int timeout = 1000; // 1 seconds
        try (var socket = new Socket()) {
            var url = new URL(urlString);
            socket.connect(new InetSocketAddress(url.getHost(), url.getPort()), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
```

here the full health check. With 1s connection timeout, 3s read timeout and TCP then API
only healthy if status = 200 and starts with "healthy". Don't do .equals(__) because there is very often a \n char at the end of the body.
```java
    private boolean isHealthy(String url) {
        if (!this.isTcpHealthy(url)) {
            return false;
        }
        try {
            var httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(3, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            var request = new Request.Builder()
                    .url(url + "/health").get().build();
            var response = httpClient.newCall(request).execute();

            int statusCode = response.code();
            var responseBody = response.body().string();
            return statusCode == 200 && responseBody.startsWith("healthy");
        } catch (Exception e) {
            return false;
        }
    }
```

### RoundRobin implementation specials

First attempt: keep the index
- keep an index of all servers, increment and % every time.
- BAD:
    - not all are healthy
    - index can be very wrong if low index backends are dying

Second: keep the url and dynamically order servers
- if server are unhealthy they are removed from the list
- if ok they come back - at the end of the list
- a linear search by url is needed and move to next one % size

Third: retry if failure during execution
- try twice to send request - if IOException then the server is not responding.
- if it would be a http-status then server is running but error in processing

```java
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            var retryCount = 2;
            while (retryCount > 0) {
                var be = loadbalancer.lbStrategy.getNext();
                if (be.isEmpty()) {
                    throw new ServletException("No backend found");
                }
                var beUrl = be.get();
                try {
                    var beResponse = this.executeRequest(beUrl, request);
                    fetchResponseAndSendBack(request, response, beUrl, beResponse);
                    return;
                } catch (IOException e) {
                    loadbalancer.lbStrategy.unhealthy(beUrl);
                    retryCount--;
                }
            }
        }
```

# Final remark

Thanks https://twitter.com/johncrickett for the great challenge

I fully recommend this challenges for all Software Engineers who would like get better
https://codingchallenges.fyi/challenges/challenge-load-balancer/

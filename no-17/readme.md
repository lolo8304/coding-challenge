# Write your own Memcached server (and client)

## install
I installed as a reference on my Mac a memcached from https://gist.github.com/tomysmile/ba6c0ba4488ea51e6423d492985a7953

## use local memcached server

I start a local memcached on port 11212 so I can use the default port for mine and in verbose mode -vv to see all the actions

```bash
/usr/local/opt/memcached/bin/memcached -l localhost -p 11212 -vv
```

to clean out data if needed
```bash
echo 'flush_all' | nc localhost 11212
```

to add and retrieve using 'echo'
```bash
echo -e "set foo 0 900 5\nhello\r" | nc localhost 11212
echo -e "get foo\r" | nc localhost 11212
echo -e "delete foo\r" | nc localhost 11212

```

## use my own memcached server

you can run the script memcached.sh to start the server and it will be started on port 11212

```bash
./app/build/install/app/bin/app -s -p 11212
```

```bash
./memcached.sh
Jul 18, 2023 11:27:40 PM memcached.Mem call
INFO: starting 'localhost' on port 11212
Jul 18, 2023 11:27:40 PM listener.Listener start
INFO: Memcached server started on port 11212
```


## use my own memcached client


I created the same main class to start a client. You can pass
-servers "list of servers (host:port) seperated by ,"

```bash
./app/build/install/app/bin/app -servers 'localhost:11212'
```

## use my own memcached main

here is the --help operation

```bash
./app/build/install/app/bin/app --help
Usage: mem [-hV] [-n=<serverName>] [-p=<port>] [-servers=<serverIds>] [-s
           [=<isServer>...]]
This challenge is to build your own memcached server
  -h, --help                 Show this help message and exit.
  -n=<serverName>            -n specifies a constant server name. default
                               localhost
  -p=<port>                  -p specifies the port. default 11211
  -s=[<isServer>...]         -s specifies if its a server or a client. default
                               false means client
      -servers=<serverIds>   -servers specifies the list of server ids
                               separated by , and only used if client mode.
                               default localhost:11211
  -V, --version              Print version information and exit.

```
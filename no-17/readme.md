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
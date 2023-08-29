# Overview

I am implementing the Build Your Own DNS resolver based on the coding challenge by https://codingchallenges.fyi/challenges/challenge-dns-resolver by John Cricket

# Language

I will use Java as I am gonna use Java again in my next job after having already experience for about 20 years but 'lost' a bit in C# in the last years.

## Template

# Prepare

# Run

- install dist using gradle and run

```bash
./run.sh -d codingchallenges.fyi

Server: 	127.0.0.53
Address:	127.0.0.53#53

Non-authoritative answer:
Name:  codingchallenges.fyi
Address:  18.67.17.9
Address:  18.67.17.33
Address:  18.67.17.90
Address:  18.67.17.95

```

- if you run with 1st level of verbose using -v. It will print out the dns location and the request domain

```bash
./run.sh -d codingchallenges.fyi -v -root -norecurse
Server: 	202.12.27.33
Address:	202.12.27.33#53

Non-authoritative answer:
Aug 18, 2023 11:57:39 P.M. dns.DnsServer lookup
INFO: Querying [ m.root-servers.net (202.12.27.33) ] for codingchallenges.fyi
Name:  codingchallenges.fyi
Address:  18.67.17.9
Address:  18.67.17.33
Address:  18.67.17.90
Address:  18.67.17.95


```

- or with -v -v to add more verbose output

```bash
./run.sh -d codingchallenges.fyi -v
Server: 	62.2.17.61
Address:	62.2.17.61#53

Non-authoritative answer:
Aug 21, 2023 9:48:18 AM dns.DnsServer lookup
INFO: Querying [ 62.2.17.61 ] for codingchallenges.fyi
Aug 21, 2023 9:48:18 AM dns.DnsServer sendAndReceive
INFO: DNS [ 62.2.17.61 ], Request: 1F360100000100000000000010636F64696E676368616C6C656E676573036679690000010001
Aug 21, 2023 9:48:18 AM dns.DnsServer sendAndReceive
INFO: DNS [ 62.2.17.61 ], Response: 1F368180000100040000000010636F64696E676368616C6C656E676573036679690000010001C00C000100010000003200040DE06702C00C000100010000003200040DE0676EC00C000100010000003200040DE06738C00C000100010000003200040DE0670E
Name:  codingchallenges.fyi
Address:  13.224.103.2
Address:  13.224.103.14
Address:  13.224.103.56
Address:  13.224.103.110
```

## Options

```bash
./run.sh --help
Usage: DnsResolver [-hV] [-d=<domain>] [-dns=<dnsServer>] [-p=<port>]
                   [-type=<typeString>] [-norecurse] [-root] [-v] [-vv]
dns resolve a domain name to IP
  -d=<domain>              -d specifies the domain name

  -dns=<dnsServer>         -dns specifies the DNS server
  -p=<port>                -p specifies the port of the dns server

  -norecurse              -norecurse specifies to not use
  -root                    -root specifies to use a root server
  -type=<typeString>       -type specifies the type of request. default all.
                            possible: a, cname, txt, mx, ns, all
  -v                       -v specifies to output verbose information with
                            level FINE
  -vv                      -vv specifies to output verbose information with
                            level FINER

  -h, --help               Show this help message and exit.
  -V, --version            Print version information and exit.
```

# Implementation

## Step 1

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

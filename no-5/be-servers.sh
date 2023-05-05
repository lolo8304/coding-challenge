#!/bin/bash

scriptpath=`dirname "$0"`

shallKill=1
if [ "-nokill" == "$1" ]; then
    shallKill=0
fi
onlyKill=0
if [ "-kill" == "$1" ]; then
    onlyKill=1
    shallKill=1
fi

if [ "1" == "$shallKill" ]; then
  pgrep -f "no-5/app" | xargs kill
  sleep 2
  ps -ef | grep "no-5/app" | grep -v "grep"
  echo "all killed"
  #echo all stopped. start now or ctrl-c
  #read
  if [ "1" == "$onlyKill" ]; then
    exit 0
  fi
fi

blist=
N=20
for i in $(seq 9000 $((9000+$N-1))); do
    # run your process here, using the value of i as an argument
    nohup ${scriptpath}/app/build/install/app/bin/app -b -p=$i > ${scriptpath}/logs/be-$i.log 2>&1 &
    if [ -z "$blist" ]; then
      blist="http://localhost:$i"
    else
      blist="${blist},http://localhost:$i"
    fi
done
echo "start all backends"

if [ "1" == "$shallKill" ]; then
  nohup ${scriptpath}/app/build/install/app/bin/app -p=8080 -blist="${blist}" > ${scriptpath}/logs/lb.log 2>&1 &
  echo "start load balancer"
fi


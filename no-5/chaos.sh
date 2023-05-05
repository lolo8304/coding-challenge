#!/bin/bash

scriptpath=`dirname "$0"`

if [ -z "$1" ]; then
	echo "Usage: chaos.sh <number of processes to delete>"
	exit 1
fi

for (( i=0; i<$1; i++ ))
do
   allpid=$(ps -ef | grep "no-5/app" | grep -v grep | grep -v "blist" | awk '{ print $2 }' | tr '\n' ' ')
   IFS=' ' read -ra str_array <<< "$allpid"

   len=${#str_array[@]}
   if [ "0" != "$len" ]; then

      random_index=$(( $RANDOM % ${#str_array[@]} ))
      random_element=${str_array[$random_index]}

      echo "kill now $random_element"
      kill $random_element
      sleep 2
   fi

done

${scriptpath}/be-servers.sh -nokill


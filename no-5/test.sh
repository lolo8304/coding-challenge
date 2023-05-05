#!/bin/bash

while true; do

	wget -qO- http://localhost:8080/hello &
	sleep 0.5

done

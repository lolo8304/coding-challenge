filename=$(./app/build/install/app/bin/app "$@" | grep "file generated" | awk -F\' '{ print $2 }')
open $filename
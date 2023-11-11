if [[ $@ == *"-v"* || $@ == *"-h"* ]]; then
  ./app/build/install/app/bin/app "$@"
else
  filename=$(./app/build/install/app/bin/app "$@" | grep "file generated" | awk -F\' '{ print $2 }')
  if [ "${filename}" != "" ]; then
    echo "QR code created: ${filename}"
    open $filename
  fi
fi


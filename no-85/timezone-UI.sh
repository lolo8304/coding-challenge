#!/bin/bash
scriptDir=$(dirname "$(readlink -f "$0")")
cd frontend || exit 1
if [ -f "package.json" ]; then
  npm install
else
  echo "No package.json found, skipping npm install."
fi
npm run
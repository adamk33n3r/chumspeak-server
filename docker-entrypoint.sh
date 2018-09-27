#!/bin/bash

if [ $# -gt 0 ]; then
    exec "$@"
fi

mkdir -p build
cd build
cmake ..
make

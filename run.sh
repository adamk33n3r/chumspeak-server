#!/bin/sh

docker run --rm -it -v $(pwd):/project -p 9987:9987 adamk33n3r/ubuntu-cmake /project/build/chumspeak-server

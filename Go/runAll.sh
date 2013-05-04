#!/bin/bash

cores=`grep -c ^processor /proc/cpuinfo`

for f in *.go
do
  o=`basename $f .go`.out
  go build -o $o $f
  ./bench.sh $o 1 $((cores + 1)) 1
  rm $o
done

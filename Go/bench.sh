#!/bin/sh

_bench_usage() {
  cat <<USAGE
Usage: bench exec n1 n2 step : Run a Go executable multiple times for incrementing values 
of `GOMAXPROCS` from `n1` to `n2` with a step of `step` and collect the timings in a 
`.dat` files.
USAGE
}

_bench_plot() {
  e=$1  
  i=$2
  dat=`basename $e .out`.dat
  echo "# "$e" : "$2" to "$3" by "$4""
  echo "# "$e" : "$2" to "$3" by "$4"" > $dat
  while [ $i -le $3 ]; do
    GOMAXPROCS=$i
    export GOMAXPROCS
    _bench_run $e
    echo $i "thread(s) :" $tim"s"
    echo $i $tim >> $dat
    i=`expr $i + 1`
  done
}

_bench_run() {
  j="0"
  local cmd="/usr/bin/time -f \"real %e\" ./$1 2>&1|grep real|sed 's/real \(.*\)/\\1/'"
  #echo $cmd
  #echo $GOMAXPROCS
  tim=`eval $cmd`
}

_bench() {
  tim="0"
  _bench_plot "$@"
}

_bench "$@"

echo
exit 0

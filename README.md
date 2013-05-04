# multicore-benchmarks

A collection of multicore benchmarks

## Running The Benchmarks

### JVM

The JVM benchmarks are located in the `JVM` directory. They are run with [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup) like this:

1. `$ cd JVM`
2. `$ sbt run`
3. Select `benchmarks.Runner`

All the `.dat` files with the timings and the Gnuplot files will be generated in the `reports` sub-directory.

## Google Go

The Google Go benchmarks are located in the `Go` directory. They are run using the `./runAll.sh` script that will generate `.dat` files with the timings in the same directory. Unfortunately, contrariliy to the JVM benchmarks, other columns like the throughput must be computed by hand.
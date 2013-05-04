# multicore-benchmarks

A collection of multicore benchmarks

## Runtimes/Languages/Libraries

- [JVM/Scala/Molecule](https://github.com/molecule-labs/molecule#molecule)
- [Google Go](http://golang.org/)

## Latest Results

Megaphone: 24-cores 64-bit (AMD Opteron 6174, 2.2 GHz per core)

### Thread-Ring

**Ref:** [Halen, J., Karlsson, R., and Nilsson, M. Performance measurements of threads in Java and
processes in Erlang.](http://www.sics.se/~joe/ericsson/du98024.htm)

![thread-ring%throughput.plt](https://raw.github.com/sbocq/multicore-benchmarks/master/reports/megaphone/comparison/thread-ring.png)

**Sources:** 
- Molecule: [ThreadRing.scala](https://github.com/sbocq/multicore-benchmarks/blob/master/JVM/src/main/scala/benchmarks/molecule/ThreadRing.scala)
- Go: [thread-ring.go](ttps://github.com/sbocq/multicore-benchmarks/blob/master/Go/thread-ring.go)

### Chameneos-Redux

**Ref:** [Kaiser, C., and Pradat-Peyre, J. Chameneos, a concurrency game for Java, Ada and others. Int.
Conf. ACS/IEEE AICCSA (2003).](http://cedric.cnam.fr/fichiers/RC474.pdf)

![chameneos-redux%throughput.plt](https://raw.github.com/sbocq/multicore-benchmarks/master/reports/megaphone/comparison/chameneos-redux.png)

**Sources:** 
- Molecule: [ChameneosReduxWord.scala](https://github.com/sbocq/multicore-benchmarks/blob/master/JVM/src/main/scala/benchmarks/molecule/ChameneosReduxWord.scala), [ChameneosReduxCore.scala](https://github.com/sbocq/multicore-benchmarks/blob/master/JVM/src/main/scala/benchmarks/molecule/ChameneosReduxCore.scala)
- Go: [thread-ring.go](ttps://github.com/sbocq/multicore-benchmarks/blob/master/Go/chameneos-redux.go)

### Prime-Sieve (N=150000)

**Ref:** [Kahn, G., and Macqueen, D. Coroutines and Networks of Parallel Processes. In Information Processing ’77: Proceedings of the IFIP Congress. North-Holland, 1977, pp. 993–998.](Coroutines and Networks of Parallel Processes)

![prime-sieve%throughput.plt](https://raw.github.com/sbocq/multicore-benchmarks/master/reports/megaphone/comparison/prime-sieve.png)

**Sources:** 
- Molecule: [PrimeSieve.scala](https://github.com/sbocq/multicore-benchmarks/blob/master/JVM/src/main/scala/benchmarks/molecule/PrimeSieve.scala)
- Go: [prime-sieve.go](ttps://github.com/sbocq/multicore-benchmarks/blob/master/Go/prime-sieve.go)

_TODO: Update this Figure because Go computed all the primes less than 100000 while Molecule computed all the ones less than 150000_.

## Running The Benchmarks

### JVM

The JVM benchmarks are located in the `JVM` directory. They are run with [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup) like this:

1. `$ cd JVM`
2. `$ sbt run`
3. Select `benchmarks.Runner`

All the `.dat` files with the timings and the Gnuplot files will be generated in the `reports` sub-directory.

### Google Go

The Google Go benchmarks are located in the `Go` directory. They are run using the `./runAll.sh` script that will generate `.dat` files with the timings in the same directory. Unfortunately, contrariliy to the JVM benchmarks, other columns like the throughput must be computed by manually.
package benchmarks

import mbench.benchmark._
import mbench.gnuplot._
import mbench.MBench.benchmarkFolder

trait Benchmarks {

  import Benchmarks._

  def threadRing(
    benchmark: Benchmark[Int, ThreadRing.Cfg, DatFile],
    config: StaticConfig[ThreadRing.Cfg]): Seq[DatFile]

  def chameneosRedux(
    benchmark: Benchmark[Int, ChameneosRedux.Cfg, DatFile],
    config: StaticConfig[ChameneosRedux.Cfg]): Seq[DatFile]

  def primeSieve(
    benchmark: Benchmark[Int, PrimeSieve.Cfg, DatFile],
    config: StaticConfig[PrimeSieve.Cfg]): Seq[DatFile]

}

object Benchmarks {

  val minDuration = 0.5 // seconds
  val warmups = 2
  val runs = 5
  val cores = mbench.Host.cores.get

  object ThreadRing {

    val name = "thread-ring"

    case class Cfg(size: Int, N: Int) {
      override def toString = "size=" + size + ", N=" + N
    }

    val config = Config.static(Cfg(503, 200000))

    val threads = (1 to 3) ++ (4 to cores by 2)

    val xlabel = Label[Int]("threads")

    val throughput = Column.withConfig[Int, Cfg, Double]("throughput", "msg".perSeconds)(
        (threads, cfg, time) => cfg.N / time
      )

    val benchmark =
      Benchmark(name, threads, xlabel, warmups, runs)
        .add(throughput)

    def settings = Seq(Plot.xtics(1))
    def labels = Seq(throughput.label)

    def plot(datfiles: Seq[DatFile]): Unit =
      Gnuplot.save(Gnuplot(datfiles, settings, labels))

    def compare(datfiles: Seq[DatFile]): Unit =
      Gnuplot.save(benchmarkFolder("comparison"), Gnuplot(name, datfiles, settings, labels))
  }


  object ChameneosRedux {

    val name = "chameneos-redux"

    case class Cfg(nbMeetings: Int, nbChameneos: Int) {
      override def toString =
        "nbMeetings=" + nbMeetings +
      ", nbChameneos=" + nbChameneos
    }

    val config = Config.static(Cfg(300000, 300))

    val xlabel = Label[Int]("threads")

    val throughput = Column.withConfig[Int, Cfg, Double]("throughput", "meetings".perSeconds)(
        (threads, config, time) => config.nbMeetings / time
      )

    val speedup = throughput.speedupHigherIsBetter

    val threads = Seq(1, 2, 3) ++ (4 to (cores) by 2) ++ Seq(2, 4).map(_ + cores)

    val benchmark =
      Benchmark(name, threads, xlabel, warmups, runs)
        .add(throughput)
        .add(speedup)

    private def settings = Seq(Plot.xtics(1))
    private def labels = Seq(throughput.label, speedup.label)

    def plot(datfiles: Seq[DatFile]): Unit =
      Gnuplot.save(Gnuplot(datfiles, settings, labels))

    def compare(datfiles: Seq[DatFile]): Unit =
      Gnuplot.save(benchmarkFolder("comparison"), Gnuplot(name, datfiles, settings, labels))

  }

  object PrimeSieve {

    val name = "prime-sieve"

    case class Cfg(N: Int) {
      override def toString = "N=" + N
    }

    def config = Config.static(Cfg(150000))

    val xlabel = Label[Int]("threads")

    val speedup = Column.timeSpeedup

    val threads = (1 to 3) ++ (4 to cores by 2)

    val benchmark =
      Benchmark(name, threads, xlabel, warmups, runs)
        .add(speedup)

    def plot(datfiles: Seq[DatFile]): Unit =
      Gnuplot.save(Gnuplot(datfiles, Plot.xtics(1), Plot.logy))

    def compare(datfiles: Seq[DatFile]): Unit =
      Gnuplot.save(benchmarkFolder("comparison"), Gnuplot(name, datfiles, Plot.xtics(1), Plot.logy))
  }

}

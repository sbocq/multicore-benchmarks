package benchmarks.molecule

object Molecule extends benchmarks.Benchmarks {

  import mbench.benchmark._
  import mbench.gnuplot._
  import molecule.platform.Platform

  import benchmarks.Benchmarks.ThreadRing.{Cfg => ThreadRingCfg}
  import benchmarks.Benchmarks.PrimeSieve.{Cfg => PrimeSieveCfg}
  import benchmarks.Benchmarks.ChameneosRedux.{Cfg => ChameneosReduxCfg}

  def minDuration = benchmarks.Benchmarks.minDuration

  val runtime =
    Config.runtime[Int, Platform]("",
      threads => Platform("molecule", nbThreads = threads),
      platform => platform.shutdownNow()
    )

  def threadRing(
    benchmark: Benchmark[Int, ThreadRingCfg, DatFile],
    config: StaticConfig[ThreadRingCfg]): Seq[DatFile] = {

    val test = Test.runtimeStatic[Platform, ThreadRingCfg]("molecule",
        (p, cfg) => ThreadRing.run(p, cfg.size, cfg.N))

    // perform a dry run to estimate the execution time
    // and shorten it to 'minDuration'. This works because we are only
    // interested in the throughput.
    val dryConfig = runtime and config
    val m = benchmark.dryRun(5, 1, dryConfig, test)
    val newConfig = config.update(cfg =>
        cfg.copy(N = (minDuration * (cfg.N / m.time)).toInt)
    )

    val fullConfig = runtime and newConfig
    Seq(benchmark(fullConfig, test))
  }

  def primeSieve(
    benchmark: Benchmark[Int, PrimeSieveCfg, DatFile],
    config: StaticConfig[PrimeSieveCfg]): Seq[DatFile] = {

    val thresholds = Seq((1, 1), (50, 50))

    val ebenchmark = benchmark.extend[(Int, Int)]

    val test =
      Test.runtimeStatic[Platform, (PrimeSieveCfg, (Int, Int))](
        "molecule",
        {case (p, (cfg, (sst, cct))) => PrimeSieve.run(p, cfg.N, sst, cct)})

    val configs = thresholds.map({
        case (sst, cct) => runtime and config.extend(
          Config.static("SST=" + sst + "-CCT=" + cct, (sst, cct)))
      })

    configs.map(ebenchmark(_, test))

  }

  def chameneosRedux(
    benchmark: Benchmark[Int, ChameneosReduxCfg, DatFile],
    config: StaticConfig[ChameneosReduxCfg]): Seq[DatFile] = {

    val testCore = Test.runtimeStatic[Platform, ChameneosReduxCfg](
        "molecule-core",
        (p, cfg) => ChameneosReduxCore.run(p, cfg.nbChameneos, cfg.nbMeetings))

    val testWord = Test.runtimeStatic[Platform, ChameneosReduxCfg](
        "molecule-word",
        (p, cfg) => ChameneosReduxCore.run(p, cfg.nbChameneos, cfg.nbMeetings))

    Seq(
      _chameneosRedux(benchmark, config, testCore),
      _chameneosRedux(benchmark, config, testWord)
    )
  }

  private def _chameneosRedux(
    benchmark: Benchmark[Int, ChameneosReduxCfg, DatFile],
    config: StaticConfig[ChameneosReduxCfg],
    test: RuntimeStaticTest[Platform, ChameneosReduxCfg]): DatFile = {

    // perform a dry run with SST = 0 to estimate the execution
    // time and shorten it to 'minDuration'
    val dryConfig = runtime and config
    val m = benchmark.dryRun(5, 1, dryConfig, test)
    val newConfig = runtime and config.update(cfg =>
      cfg.copy(nbMeetings = (minDuration * (cfg.nbMeetings / m.time)).toInt))

    benchmark(newConfig, test)
  }

}

package benchmarks

object Runner {

  import mbench.benchmark._

  val benchmarks:Seq[Benchmarks] = Seq(
      molecule.Molecule
    )

  import Benchmarks._

  def main(args:Array[String]) {

    ThreadRing.compare(
      benchmarks.flatMap(
        _.threadRing(ThreadRing.benchmark, ThreadRing.config)))

    ChameneosRedux.compare(
      benchmarks.flatMap(
        _.chameneosRedux(ChameneosRedux.benchmark, ChameneosRedux.config)))

    PrimeSieve.compare(
      benchmarks.flatMap(
        _.primeSieve(PrimeSieve.benchmark, PrimeSieve.config)))
  }

}

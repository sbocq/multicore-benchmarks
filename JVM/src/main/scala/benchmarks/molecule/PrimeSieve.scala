package benchmarks.molecule

import molecule._
import molecule.io._

object PrimeSieve extends ProcessType1x1[Int, Int, Unit] {

  def main(ints: Input[Int], log: Output[Int]) =
    for {
      prime <- ints.read() orCatch {
        case EOS => shutdown()
      }
      _ <- log.write(prime)
      _ <- handover(PrimeSieve(ints.filter(_ % prime != 0), log))
    } yield ()

  import molecule.platform.Platform
  import molecule.channel.Console

  def run(platform: Platform, N: Int) {

    val ints = channel.IChan(2 to N)
    val log = Console.logOut[Int]("log")

    platform.launch(PrimeSieve(ints, log)).get_!()
  }

  def main(args: Array[String]): Unit = {
    val platform = Platform("prime-sieve")
    run(platform, 150000)
  }

  def run(platform: Platform, N: Int, SST:Int, CCT:Int) {

    Platform._complexityCutoffThreshold = CCT

    val ints = channel.IChan.source(2 to N, SST)
    val log = Console.logOut[Int]("log")

    platform.launch(PrimeSieve(ints, log)).get_!()
  }

}

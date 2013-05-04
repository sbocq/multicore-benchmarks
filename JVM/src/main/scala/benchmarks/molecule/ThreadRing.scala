package benchmarks.molecule

import molecule._
import molecule.io._
import molecule.stream._
import molecule.channel.{ Chan, RChan, ROChan }

object ThreadRing {
  class Id(val label: Int, val rchan: ROChan[Int]) {
    def found(): IO[Unit] = rchan.success(label)
  }

  object Worker extends ProcessType2x1[Id, Int, Int, Unit] {
    def main(id: Input[Id], prev: Input[Int], next: Output[Int]) =
      prev.foreach { i =>
        if (i == 0)
          id.read() >>\ { _.found() } >> next.close()
        else
          next.write(i - 1)
      }
  }

  import molecule.platform.Platform

  def run(platform: Platform, ringSize: Int, initVal: Int) {
    val (first_i, last_o) = Chan.mk[Int]()
    val (ri, ro) = RChan.mk[Int]()

    val last_i = (1 until ringSize).foldLeft(initVal :: first_i) {
      case (prev, label) =>
        val (nextPrev, next) = Chan.mk[Int]()
        platform.launch(Worker(new Id(label, ro).asI, prev, next))
        nextPrev
    }

    platform.launch(Worker(new Id(ringSize, ro).asI, last_i, last_o))

    println(ri.get_!())
  }

  def main(args: Array[String]): Unit = {
    val platform = Platform("ring")
    run(platform, 503, 2000000)
  }
}

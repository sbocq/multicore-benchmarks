package benchmarks.molecule

import molecule._
import molecule.io._
import molecule.request._
import molecule.request.io._

object ChameneosReduxWord {

  sealed abstract class Color
  case object Blue extends Color
  case object Red extends Color
  case object Yellow extends Color

  val colorTypes = Array(Blue, Red, Yellow)

  case class ChameneoId(id: Int, color: Color)
  case class ChameneoReport(nbMeetings: Int)
  abstract class ChameneoMessage

  case class MeetRequest(peer: ChameneoId)(val rchan: ResponseChannel[Copy]) extends ChameneoMessage with Response[Copy]
  case class Copy(peer: ChameneoId) extends ChameneoMessage

  case class MallRequest(id: ChameneoId)(val rchan: ResponseChannel[ChameneoMessage]) extends Response[ChameneoMessage]

  object Mall extends ProcessType2x0[Int, MallRequest, Unit] {

    def main(cfg: Input[Int], requests: Input[MallRequest]) =
      for {
        nbMeetings <- cfg.read()
        _ <- requests.grouped(2).take(nbMeetings).foreach { mates =>
          val Seg(first, second) = mates
          first.rchan.reply(MeetRequest(second.id)(second.rchan))
        }
      } yield ()
  }

  object Chameneo extends ProcessType1x1[ChameneoId, MallRequest, Int] {

    private def complement(c1: Color, c2: Color) = (c1, c2) match {
      case (Blue, Blue) => Blue
      case (Blue, Red) => Yellow
      case (Blue, Yellow) => Red
      case (Red, Blue) => Yellow
      case (Red, Red) => Red
      case (Red, Yellow) => Blue
      case (Yellow, Blue) => Red
      case (Yellow, Red) => Blue
      case (Yellow, Yellow) => Yellow
    }

    def main(cfg: Input[ChameneoId], mall: Output[MallRequest]) = {

      def behave(nbMeetings: Int, self: ChameneoId, exit: Int => IO[Nothing]): IO[Nothing] = { // Infinite loop

        mall.request(MallRequest(self)).orCatch { case EOS => exit(nbMeetings) } >>\ {
          case req @ MeetRequest(other) =>
            val newColor = complement(self.color, other.color)
            val newSelf = self.copy(color = newColor)

            req.rchan.reply(Copy(newSelf)) >> behave(nbMeetings + 1, newSelf, exit)
          case Copy(other) =>
            val newSelf = self.copy(color = other.color)

            behave(nbMeetings + 1, newSelf, exit)
        }
      }
      for {
        id <- cfg.read()
        nbMeetings <- callcc[Int] ( behave(0, id, _) ) // call using current continuation expecting an Int
      } yield nbMeetings
    }
  }

  import molecule.channel.OChanFactory

  object ChameneosRedux extends ProcessType1x0[(Seq[Color], OChanFactory[MallRequest]), Unit] {
    override def name = "ChameneosRedux"

    def main(cfg: Input[(Seq[Color], OChanFactory[MallRequest])]) = {

      def start(colors: Seq[Color], mkMallCh: OChanFactory[MallRequest]): IO[Unit] = {

        val chameneos: Seq[IO[Int]] = colors.zipWithIndex.map {
          case (color, i) =>

            val id = ChameneoId(i, color)
            launch(Chameneo(id.asI, mkMallCh())).get()
        }

        parl(chameneos) >>\ (rs => ioLog(rs.sum.toString))
      }

      for {
        (colors, mallCh) <- cfg.read()
        _ <- start(colors, mallCh)
      } yield ()
    }
  }

  import molecule.platform.Platform
  import molecule.channel.{ Chan, ManyToOne }

  def run(platform: Platform, nbChameneos: Int, nbMeetings: Int): Unit = {
    assert(nbChameneos > 1, "Number of chameneos should be greater than 1")

    val (ichan, mkOChan) = ManyToOne.mk[MallRequest]()

    platform.launch(Mall(nbMeetings.asI, ichan))

    val colors = (0 until nbChameneos) map { i => colorTypes(i % colorTypes.length) }

    platform.launch(ChameneosRedux((colors, mkOChan).asI)).get_!()

  }

  def main(args: Array[String]): Unit = {
    val platform = Platform("chameneos-redux", nbThreads = 12)
    run(platform, 300, 300000)
  }
}

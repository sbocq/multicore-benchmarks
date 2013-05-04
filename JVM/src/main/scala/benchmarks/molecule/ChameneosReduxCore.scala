package benchmarks.molecule

import molecule._
import molecule.request._
import molecule.request.core._
import molecule.channel.RIChan
import molecule.stream._

object ChameneosReduxCore {

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

  import process.CoreProcess

  def mall(nbMeetings: Int, requests: IChan[MallRequest]) =
    CoreProcess.singleton("mall", {

      requests.grouped(2).take(nbMeetings).foreach { mates =>
        val Seg(first, second) = mates
        first.rchan.reply_!(MeetRequest(second.id)(second.rchan))
      }

    })

  val chameneo =
    CoreProcess.factory[(ChameneoId, OChan[MallRequest]), Int]("chameneo", {
      case ((id, mall), t) =>

        def complement(c1: Color, c2: Color) = (c1, c2) match {
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

        def behave(mall: OChan[MallRequest], nbMeetings: Int, self: ChameneoId, exit: Int => RIChan[Nothing]): RIChan[Nothing] = {
          mall.request(t, MallRequest(self)).orCatch { case RequestSignal(_, EOS) => exit(nbMeetings) }.flatMap {
            case (mall, result) => result match {
              case req @ MeetRequest(other) =>
                val newColor = complement(self.color, other.color)
                val newSelf = self.copy(color = newColor)

                req.rchan.reply_!(Copy(newSelf))
                behave(mall, nbMeetings + 1, newSelf, exit)

              case Copy(other) =>
                val newSelf = self.copy(color = other.color)

                behave(mall, nbMeetings + 1, newSelf, exit)
            }
          }
        }

        RIChan.callcc(behave(mall, 0, id, _))
    })

  import molecule.platform.Platform
  import molecule.channel.{ Chan, ManyToOne }

  def run(platform: Platform, nbChameneos: Int, nbMeetings: Int): Unit = {
    assert(nbChameneos > 1, "Number of chameneos should be greater than 1")

    val (ichan, mkOChan) = ManyToOne.mk[MallRequest]()

    platform.launch(mall(nbMeetings, ichan))

    val colors = (0 until nbChameneos) map { i => colorTypes(i % colorTypes.length) }

    val chameneos: Seq[RIChan[Int]] = colors.zipWithIndex.map {
      case (color, i) =>
        val id = ChameneoId(i, color)
        platform.launch(chameneo(id, mkOChan()))
    }

    val report = RIChan.parl(chameneos).map { _.sum }.get_!()
    println(report)
  }

  def main(args: Array[String]): Unit = {
    val platform = Platform("chameneos-redux")
    run(platform, 300, 300000)
  }
}

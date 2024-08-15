package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsEdgedetect extends App {
  Config
    .spinal("Edgedetect.v") // set the output file name
    .generateVerilog(
      HdlBitsEdgedetect().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Edgedetect
case class HdlBitsEdgedetect() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val cin =
      in Bits (8 bits) // `in` is a reserved keyword in Scala, so we use `cin` instead
    val pedge = out Bits (8 bits)
  }

  // Configure the clock domain
  val myClockDomain = ClockDomain(
    clock = io.clk,
    config = ClockDomainConfig(
      clockEdge = RISING
    )
  )

  // Define an Area which use myClockDomain
  val myArea = new ClockingArea(myClockDomain) {
    val cinState = RegNext(io.cin)
    val pedgeState = RegNext(io.cin & ~cinState)
    io.pedge := pedgeState
  }
}

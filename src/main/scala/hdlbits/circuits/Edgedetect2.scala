package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsEdgedetect2 extends App {
  Config
    .spinal("Edgedetect2.v") // set the output file name
    .generateVerilog(
      HdlBitsEdgedetect2().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Edgedetect2
case class HdlBitsEdgedetect2() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val cin =
      in Bits (8 bits) // `in` is a reserved keyword in Scala, so we use `cin` instead
    val anyedge = out Bits (8 bits)
  }

  // Define an Area which use a custom ClockDomain
  new ClockingArea(
    ClockDomain(
      clock = io.clk,
      config = ClockDomainConfig(
        clockEdge = RISING
      )
    )
  ) {
    val cinState = RegNext(io.cin)
    val anyedgeState = RegNext(io.cin ^ cinState)
    io.anyedge := anyedgeState
  }
}

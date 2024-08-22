package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsEdgecapture extends App {
  Config
    .spinal("Edgecapture.v") // set the output file name
    .generateVerilog(
      HdlBitsEdgecapture().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Edgecapture
case class HdlBitsEdgecapture() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool ()
    val cin = in UInt (32 bits)
    val cout = out UInt (32 bits)
  }

  val coutArea = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      reset = io.reset,
      config = ClockDomainConfig(
        clockEdge = RISING,
        resetKind = SYNC,
        resetActiveLevel = HIGH
      )
    )
  ) {
    // Register to hold the delayed input
    val inDly = Reg(UInt(32 bits))

    // Register to hold the output
    val coutReg = Reg(UInt(32 bits)) init (0)

    inDly := io.cin
    coutReg := ~io.cin & inDly | coutReg
  }

  io.cout := coutArea.coutReg
}

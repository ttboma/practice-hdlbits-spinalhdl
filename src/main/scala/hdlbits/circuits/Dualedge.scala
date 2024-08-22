package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsDualedge extends App {
  Config
    .spinal("Dualedge.v") // set the output file name
    .generateVerilog(
      HdlBitsDualedge().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Dualedge
case class HdlBitsDualedge() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool ()
    val d = in Bool ()
    val q = out Bool ()
  }

  // NOTE: dual edge flip-flop which capture data on both the rising and falling edges of the clock, are not natively synthesizable in most standard FPGA or ASIC synthesis flows.
  //  And clock domain crossing issues can be problematic in hardware design, especially when dealing with signals that transition between different clock domains. The challenges primarily arise from timing issues and metastability.
  val dualedge = new HdlBitsDualedgeFlipFlop()

  dualedge.io.clk := io.clk
  dualedge.io.d := io.d
  io.q := dualedge.io.q
}

class HdlBitsDualedgeFlipFlop extends BlackBox {
  val io = new Bundle {
    val clk = in Bool ()
    val d = in Bool ()
    val q = out Bool ()
  }

  // Remove io_ prefix
  noIoPrefix()

  addRTLPath("Dualedge.v")
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsMt2015Muxdff extends App {
  Config
    .spinal("Mt2015Muxdff.v") // set the output file name
    .generateVerilog(
      HdlBitsMt2015Muxdff().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Mt2015_muxdff
case class HdlBitsMt2015Muxdff() extends Component {
  val io = new Bundle {
    val clk, L, r_in, q_in = in Bool ()
    val Q = out Bool ()
  }

  val c0 = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      config = ClockDomainConfig(
        clockEdge = RISING
      )
    )
  ) {
    val reg = Reg(Bool)
    reg := io.L.mux(io.r_in, io.q_in)
  }

  io.Q := c0.reg
}

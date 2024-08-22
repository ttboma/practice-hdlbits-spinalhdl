package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsLfsr5 extends App {
  Config
    .spinal("Lfsr5.v") // set the output file name
    .generateVerilog(
      HdlBitsLfsr5().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Lfsr5
case class HdlBitsLfsr5() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Active-high synchronous reset to 5'h1
    val q = out Bits (5 bits)
  }

  val c0 = new ClockingArea(
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
    val reg = Reg(Bits(5 bits)) init (0x1)
    reg(0) := reg(1)
    reg(1) := reg(2)
    reg(2) := reg(3) ^ reg(0)
    reg(3) := reg(4)
    reg(4) := False ^ reg(0)
  }

  io.q := c0.reg
}

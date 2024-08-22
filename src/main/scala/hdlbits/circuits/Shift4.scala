package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsShift4 extends App {
  Config
    .spinal("Shift4.v") // set the output file name
    .generateVerilog(
      HdlBitsShift4().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Shift4
case class HdlBitsShift4() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val areset = in Bool () // async active-high reset to zero
    val load, ena = in Bool ()
    val data = in UInt (4 bits)
    val q = out UInt (4 bits)
  }

  val c0 = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      reset = io.areset,
      config = ClockDomainConfig(
        clockEdge = RISING,
        resetKind = ASYNC,
        resetActiveLevel = HIGH
      )
    )
  ) {
    val reg = Reg(UInt(4 bits)) init (0)

    when(io.load) {
      reg := io.data
    } elsewhen (io.ena) {
      reg := reg |>> 1
    }
  }

  io.q := c0.reg
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsShift18 extends App {
  Config
    .spinal("Shift18.v") // set the output file name
    .generateVerilog(
      HdlBitsShift18().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Shift18
case class HdlBitsShift18() extends Component {
  val io = new Bundle {
    val clk, load, ena = in Bool ()
    val amount = in Bits (2 bits)
    val data = in SInt (64 bits)
    val q = out SInt (64 bits)
  }

  val c0 = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      config = ClockDomainConfig(
        clockEdge = RISING
      )
    )
  ) {
    val reg = Reg(SInt(64 bits))

    when(io.load) {
      reg := io.data
    } elsewhen (io.ena) {
      switch(io.amount) {
        is(0) {
          reg := reg |<< 1
        }
        is(1) {
          reg := reg |<< 8
        }
        is(2) {
          reg := reg >> U(1)
        }
        is(3) {
          reg := reg >> U(8)
        }
      }
    }
  }

  io.q := c0.reg
}

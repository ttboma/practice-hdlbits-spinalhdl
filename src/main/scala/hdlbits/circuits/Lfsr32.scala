package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsLfsr32 extends App {
  Config
    .spinal("Lfsr32.v") // set the output file name
    .generateVerilog(
      HdlBitsLfsr32(32, Array(32, 22, 2, 1))
        .noIoPrefix()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Lfsr32
case class HdlBitsLfsr32(width: Int, taps: Array[Int]) extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Active-high synchronous reset to 32'h1
    val q = out Bits (width bits)
  }

  val clockRoot = new ClockingArea(
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
    val reg = Reg(Bits(width bits)) init (0x1)
    val hasTaps = Array.fill(width)(false)
    taps.foreach(tap => if (tap >= 1 && tap <= width) hasTaps(tap - 1) = true)

    for (i <- 0 until width - 1) {
      reg(i) := {
        if (hasTaps(i)) reg(i + 1) ^ reg(0)
        else reg(i + 1)
      }
    }

    reg(width - 1) := {
      if (hasTaps(width - 1)) reg(0) ^ False
      else False
    }
  }

  io.q := clockRoot.reg
}

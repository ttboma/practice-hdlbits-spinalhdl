package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsRule90 extends App {
  Config
    .spinal("Rule90.v") // set the output file name
    .generateVerilog(
      HdlBitsRule90()
        .noIoPrefix()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Rule90
case class HdlBitsRule90() extends Component {
  val io = new Bundle {
    val clk, load = in Bool ()
    val data = in Bits (512 bits)
    val q = out Bits (512 bits)
  }

  val regBoundary = B"2'b00"

  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      config = ClockDomainConfig(
        clockEdge = RISING
      )
    )
  ) {
    val reg = Reg(Bits(512 bits))

    reg(0) := regBoundary(0) ^ reg(1)
    reg(511) := regBoundary(1) ^ reg(510)

    for (i <- 1 until 511) {
      reg(i) := reg(i - 1) ^ reg(i + 1)
    }

    when(io.load) {
      reg := io.data
    }
  }

  io.q := clockRoot.reg
}

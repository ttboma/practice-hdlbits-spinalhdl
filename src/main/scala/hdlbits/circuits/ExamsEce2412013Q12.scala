package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsEce2412013Q12 extends App {
  Config
    .spinal("ExamsEce2412013Q12.v") // set the output file name
    .generateVerilog(
      HdlBitsExamsEce2412013Q12()
        .noIoPrefix()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2013_q12
case class HdlBitsExamsEce2412013Q12() extends Component {
  val io = new Bundle {
    val clk, enable, S, A, B, C = in Bool ()
    val Z = out Bool ()
  }

  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      config = ClockDomainConfig(clockEdge = RISING)
    )
  ) {
    val reg = Reg(Bits(8 bits))

    when(io.enable) {
      reg(0) := io.S
      for (i <- 1 until 8) {
        reg(i) := reg(i - 1)
      }
    }
  }

  switch(io.A ## io.B ## io.C) {
    for (i <- 0 until 8) {
      is(i) {
        io.Z := clockRoot.reg(i)
      }
    }
  }
}

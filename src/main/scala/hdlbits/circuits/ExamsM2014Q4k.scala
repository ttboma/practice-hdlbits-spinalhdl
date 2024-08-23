package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsM2014Q4k extends App {
  Config
    .spinal("ExamsM2014Q4k.v") // set the output file name
    .generateVerilog(
      HdlBitsExamsM2014Q4k()
        .noIoPrefix()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/m2014_q4k
case class HdlBitsExamsM2014Q4k() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val resetn = in Bool () // synchronous reset
    val cin =
      in Bool () // `in` is a reserved keyword in Scala, so use `cin` instead
    val cout =
      out Bool () // `out` is a reserved keyword in Scala, so use `cout` instead
  }

  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      reset = io.resetn,
      config = ClockDomainConfig(
        clockEdge = RISING,
        resetKind = SYNC,
        resetActiveLevel = LOW
      )
    )
  ) {
    val reg = Reg(Bits(4 bits)) init (0x0)

    reg(0) := reg(1)
    reg(1) := reg(2)
    reg(2) := reg(3)
    reg(3) := io.cin
  }

  io.cout := clockRoot.reg(0)
}

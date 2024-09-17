package hdlbits.building_larger_circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsReview2015Count1k extends App {
  Config
    .spinal("ExamsReview2015Count1k.v") // set the output file name
    .generateVerilog(
      HdlBitsExamsReview2015Count1k()
        .noIoPrefix()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/review2015_count1k
case class HdlBitsExamsReview2015Count1k() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val q = out UInt (10 bits)
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
    val counter = Reg(UInt(10 bits)) init (0)

    when(counter === 999) {
      counter := 0
    } otherwise {
      counter := counter + 1
    }

    io.q := counter
  }
}

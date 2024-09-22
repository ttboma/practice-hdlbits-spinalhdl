package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsEce2412013Q8 extends App {
  Config
    .spinal("ExamsEce2412013Q8.v") // set the output file name
    .generateVerilog(
      HdlBitsExamsEce2412013Q8().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2013_q8
case class HdlBitsExamsEce2412013Q8() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val aresetn = in Bool ()
    val x = in Bool ()
    val z = out Bool ()
  }

  // State register with clock and reset
  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      reset = io.aresetn,
      config = ClockDomainConfig(
        clockEdge = RISING,
        resetKind = ASYNC,
        resetActiveLevel = LOW
      )
    )
  ) {
    val state = Reg(UInt(2 bits)) init (0)
  }

  // State transition logic
  switch(clockRoot.state) {
    is(0) {
      when(io.x) {
        clockRoot.state := 1
      }
    }
    is(1) {
      when(!io.x) {
        clockRoot.state := 2
      }
    }
    is(2) {
      when(io.x) {
        clockRoot.state := 1
      } otherwise {
        clockRoot.state := 0
      }
    }
  }

  // Output logic
  io.z := io.x && clockRoot.state === 2
}

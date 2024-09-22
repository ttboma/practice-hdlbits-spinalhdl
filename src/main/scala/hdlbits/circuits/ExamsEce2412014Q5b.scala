package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsEce2412014Q5b extends App {
  Config
    .spinal("ExamsEce2412014Q5b.v") // set the output file name
    .generateVerilog(
      HdlBitsExamsEce2412014Q5b().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2014_q5b
case class HdlBitsExamsEce2412014Q5b() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val areset = in Bool ()
    val x = in Bool ()
    val z = out Bool ()
  }

  // State register with clock and reset
  val clockRoot = new ClockingArea(
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
    val state = Reg(UInt(1 bits)) init (0)
  }

  // State transition logic
  switch(clockRoot.state) {
    is(0) {
      clockRoot.state := Mux(io.x, U(1, 1 bits), U(0, 1 bits))
    }
  }

  // Output logic
  io.z := (clockRoot.state === 0 && io.x) || (clockRoot.state === 1 && !io.x)
}

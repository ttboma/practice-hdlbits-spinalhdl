package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogExams2014Q3bfsm extends App {
  Config
    .spinal("Exams2014Q3bfsm.v") // set the output file name
    .generateVerilog(
      HdlBitsExams2014Q3bfsm().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/2014_q3bfsm
case class HdlBitsExams2014Q3bfsm() extends Component {
  val io = new Bundle {
    val x = in Bool ()
    val z = out Bool ()
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
  }

  // State register with clock and reset
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
    // State registers with clock and reset
    val state = Reg(UInt(3 bits)) init (0)
  }
  import clockRoot._

  // State transition logic
  switch(state) {
    is(0) {
      state := Mux(io.x, U(1, 3 bits), U(0, 3 bits))
    }
    is(1) {
      state := Mux(io.x, U(4, 3 bits), U(1, 3 bits))
    }
    is(2) {
      state := Mux(io.x, U(1, 3 bits), U(2, 3 bits))
    }
    is(3) {
      state := Mux(io.x, U(2, 3 bits), U(1, 3 bits))
    }
    is(4) {
      state := Mux(io.x, U(4, 3 bits), U(3, 3 bits))
    }
  }

  // Output logic
  io.z := (state === 4) || (state === 3)
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExams2014Q3fsm extends App {
  Config
    .spinal("Exams2014Q3fsm.v") // set the output file name
    .generateVerilog(
      HdlBitsExams2014Q3fsm().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsExams2014Q3fsmState extends SpinalEnum {
  val A, B0, B1, B2 = newElement()
}

import HdlBitsExams2014Q3fsmState._

// https://hdlbits.01xz.net/wiki/Exams/2014_q3_fsm
case class HdlBitsExams2014Q3fsm() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val s, w = in Bool ()
    val z = out Bool ()
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
    val state = RegInit(A)
    val counter = Reg(UInt(2 bits)) init (0)
  }

  // State transition logic
  switch(clockRoot.state) {
    is(A) {
      clockRoot.state := Mux(io.s, B0, A)
    }
    is(B0) {
      clockRoot.state := B1
      clockRoot.counter := Mux(io.w, U(1, 2 bits), U(0, 2 bits))
    }
    is(B1) {
      clockRoot.state := B2
      clockRoot.counter := Mux(io.w, clockRoot.counter + 1, clockRoot.counter)
    }
    is(B2) {
      clockRoot.state := B0
      clockRoot.counter := Mux(io.w, clockRoot.counter + 1, clockRoot.counter)
    }
  }

  // Output logic
  io.z := clockRoot.state === B0 && clockRoot.counter === 2
}

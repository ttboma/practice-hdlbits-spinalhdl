package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogExams2013Q2bfsm extends App {
  Config
    .spinal("Exams2013Q2bfsm.v") // set the output file name
    .generateVerilog(
      HdlBitsExams2013Q2bfsm().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsExams2013Q2bfsmState extends SpinalEnum {
  val a, b, c, d, e, f, g, h, i = newElement()
}

import HdlBitsExams2013Q2bfsmState._

// https://hdlbits.01xz.net/wiki/Exams/2013_q2bfsm
case class HdlBitsExams2013Q2bfsm() extends Component {
  val io = new Bundle {
    val x = in Bool ()
    val y = in Bool ()
    val f = out Bool ()
    val g = out Bool ()
    val clk = in Bool ()
    val resetn = in Bool () // active-low synchronous reset
  }

  // State register with clock and reset
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
    // State registers with clock and reset
    val state = RegInit(a)
  }
  import clockRoot._

  // State transition logic
  switch(state) {
    is(a) {
      state := b
    }
    is(b) {
      state := c
    }
    is(c) {
      when(io.x) {
        state := d
      }
    }
    is(d) {
      when(!io.x) {
        state := e
      }
    }
    is(e) {
      when(!io.x) {
        state := c
      } otherwise {
        state := f
      }
    }
    is(f) {
      when(!io.y) {
        state := g
      } otherwise {
        state := h
      }
    }
    is(g) {
      when(!io.y) {
        state := i
      } otherwise {
        state := h
      }
    }
  }

  // Output logic
  io.f := state === b
  io.g := state === f || state === g || state === h
}

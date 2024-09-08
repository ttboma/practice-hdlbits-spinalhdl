package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsm2 extends App {
  Config
    .spinal("Fsm2.v") // set the output file name
    .generateVerilog(
      HdlBitsFsm2().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Fsm2
case class HdlBitsFsm2() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val areset = in Bool () // Asynchronous reset to OFF
    val j, k = in Bool ()
    val cout =
      out Bool () // 'out' is a reserved keyword, so we use 'cout' instead
  }

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
    val state = Reg(Bool()) init (False)
    val nextState = Bool()

    switch(state) {
      is(False) {
        nextState := io.j.mux(
          True,
          False
        )
      }
      is(True) {
        nextState := io.k.mux(
          False,
          True
        )
      }
    }

    state := nextState
    io.cout := state
  }
}

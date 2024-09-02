package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsm1 extends App {
  Config
    .spinal("Fsm1.v") // set the output file name
    .generateVerilog(HdlBitsFsm1().noIoPrefix().setDefinitionName("top_module"))
}

// https://hdlbits.01xz.net/wiki/Fsm1
case class HdlBitsFsm1() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val areset = in Bool () // Asynchronous reset to state B
    val cin = in Bool () // 'in' is a reserved keyword, so we use 'cin' instead
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
    val state = Reg(Bool()) init (True)
    val nextState = Bool()

    switch(state) {
      is(False) {
        nextState := io.cin.mux(
          False,
          True
        )
      }
      is(True) {
        nextState := io.cin.mux(
          True,
          False
        )
      }
    }

    state := nextState
    io.cout := state
  }
}

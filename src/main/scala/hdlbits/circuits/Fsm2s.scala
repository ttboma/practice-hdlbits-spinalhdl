package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsm2s extends App {
  Config
    .spinal("Fsm2s.v") // set the output file name
    .generateVerilog(
      HdlBitsFsm2s().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Fsm2s
case class HdlBitsFsm2s() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset to OFF
    val j, k = in Bool ()
    val cout =
      out Bool () // 'out' is a reserved keyword, so we use 'cout' instead
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

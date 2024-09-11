package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsm3s extends App {
  Config
    .spinal("Fsm3s.v") // set the output file name
    .generateVerilog(
      HdlBitsFsm3s().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Fsm3s
case class HdlBitsFsm3s() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset to state A
    val cin = in Bool () // 'in' is a reserved keyword, so we use 'cin' instead
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
    val A = B"2'b00"
    val B = B"2'b01"
    val C = B"2'b10"
    val D = B"2'b11"

    val state = Reg(Bits(2 bits)) init (A)
    val nextState = Bits(2 bits)

    // State transition logic
    state := nextState

    // State flip-flops with asynchronous reset
    switch(state) {
      default { // is(A)
        nextState := io.cin.mux(
          B,
          A
        )
      }
      is(B) {
        nextState := io.cin.mux(
          B,
          C
        )
      }
      is(C) {
        nextState := io.cin.mux(
          D,
          A
        )
      }
      is(D) {
        nextState := io.cin.mux(
          B,
          C
        )
      }
    }

    // Output logic
    io.cout := state === D
  }
}

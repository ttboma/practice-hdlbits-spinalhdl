package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsm3 extends App {
  Config
    .spinal("Fsm3.v") // set the output file name
    .generateVerilog(
      HdlBitsFsm3().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Fsm3
case class HdlBitsFsm3() extends Component {
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
    switch(state) {
      default { // is(A)
        io.cout := False
      }
      is(B) {
        io.cout := False
      }
      is(C) {
        io.cout := False
      }
      is(D) {
        io.cout := True
      }
    }
  }
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsm3comb extends App {
  Config
    .spinal("Fsm3comb.v") // set the output file name
    .generateVerilog(
      HdlBitsFsm3comb().noIoPrefix().setDefinitionName("top_module")
    )
}

// NOTE: clk is not used in the following implementation. This is a just a combinational circuit, not a FSM.
// https://hdlbits.01xz.net/wiki/Fsm3comb
case class HdlBitsFsm3comb() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val state = in Bits (2 bits)
    val next_state = out Bits (2 bits)
    val cin = in Bool () // 'in' is a reserved keyword, so we use 'cin' instead
    val cout =
      out Bool () // 'out' is a reserved keyword, so we use 'cout' instead
  }

  val A = B"2'b00"
  val B = B"2'b01"
  val C = B"2'b10"
  val D = B"2'b11"

  switch(io.state) {
    default { // A
      io.next_state := io.cin.mux(
        B,
        A
      )
      io.cout := False
    }
    is(B) {
      io.next_state := io.cin.mux(
        B,
        C
      )
      io.cout := False
    }
    is(C) {
      io.next_state := io.cin.mux(
        D,
        A
      )
      io.cout := False
    }
    is(D) {
      io.next_state := io.cin.mux(
        B,
        C
      )
      io.cout := True
    }
  }

}

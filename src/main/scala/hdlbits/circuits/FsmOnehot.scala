package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsmOnehot extends App {
  Config
    .spinal("FsmOnehot.v") // set the output file name
    .generateVerilog(
      HdlBitsFsmOnehot().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Fsm_onehot
case class HdlBitsFsmOnehot() extends Component {
  val io = new Bundle {
    val cin = in Bool () // 'in' is a reserved keyword, so we use 'cin' instead
    val state = in Bits (10 bits)
    val next_state = out Bits (10 bits)
    val out1, out2 = out Bool ()
  }
  // State transition logic
  io.next_state(0) := ~io.cin & (io.state(0) | io.state(1) | io.state(2) | io
    .state(3) | io.state(4) | io.state(7) | io.state(8) | io.state(9))
  io.next_state(1) := io.cin & (io.state(0) | io.state(8) | io.state(9))
  io.next_state(2) := io.cin & io.state(1)
  io.next_state(3) := io.cin & io.state(2)
  io.next_state(4) := io.cin & io.state(3)
  io.next_state(5) := io.cin & io.state(4)
  io.next_state(6) := io.cin & io.state(5)
  io.next_state(7) := io.cin & (io.state(6) | io.state(7))
  io.next_state(8) := ~io.cin & io.state(5)
  io.next_state(9) := ~io.cin & io.state(6)

  // Output logic
  io.out1 := io.state(8) | io.state(9)
  io.out2 := io.state(9) | io.state(7)
}

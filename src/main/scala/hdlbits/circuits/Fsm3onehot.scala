package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsm3onehot extends App {
  Config
    .spinal("Fsm3onehot.v") // set the output file name
    .generateVerilog(
      HdlBitsFsm3onehot().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Fsm3onehot
case class HdlBitsFsm3onehot() extends Component {
  val io = new Bundle {
    val state = in Bits (4 bits)
    val next_state = out Bits (4 bits)
    val cin = in Bool () // 'in' is a reserved keyword, so we use 'cin' instead
    val cout =
      out Bool () // 'out' is a reserved keyword, so we use 'cout' instead
  }

  io.next_state(0) := (io.state(0) & ~io.cin) | (io.state(2) & ~io.cin)
  io.next_state(1) := (io.state(0) & io.cin) | (io.state(1) & io.cin) | (io
    .state(3) & io.cin)
  io.next_state(2) := (io.state(1) & ~io.cin) | (io.state(3) & ~io.cin)
  io.next_state(3) := (io.state(2) & io.cin)

  io.cout := io.state(3)
}

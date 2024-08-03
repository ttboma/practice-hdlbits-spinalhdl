package hdlbits.verilog_language

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsVectorGates extends App {
  Config.spinal("VectorGates.v") // set the output file name
    .generateVerilog(HdlBitsVectorGates())
}

// https://hdlbits.01xz.net/wiki/Vectorgates
case class HdlBitsVectorGates() extends Component {
  // Set the name of the generated module name
  setDefinitionName("top_module")

  val io = new Bundle {
    val a, b = in Bits(3 bits)
    val out_or_bitwise = out Bits(3 bits)
    val out_or_logical = out Bool()
    val out_not = out Bits(6 bits)
  }

  // Explicitly set the names. Or else the names will have implicitly `io_` prefix
  io.elements.foreach { case (name, signal) =>
    signal.setName(name)
  }

  io.out_or_bitwise := io.a | io.b
  io.out_or_logical := io.a.orR || io.b.orR
  io.out_not(2 downto 0) := ~io.a
  io.out_not(5 downto 3) := ~io.b
}

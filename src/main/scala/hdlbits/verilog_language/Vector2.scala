package hdlbits.verilog_language

import spinal.core._
import spinal.core.sim._

// https://hdlbits.01xz.net/wiki/Vector2
case class HDLBitsVector2() extends Component {
  val io = new Bundle {
    val inData = in UInt(32 bits)
    val outData = out UInt(32 bits)
  }

  // Reversing the bytes
  io.outData(31 downto 24) := io.inData(7 downto 0)
  io.outData(23 downto 16) := io.inData(15 downto 8)
  io.outData(15 downto 8) := io.inData(23 downto 16)
  io.outData(7 downto 0) := io.inData(31 downto 24)

  // Set the name of the generated module name
  setDefinitionName("top_module")
  
  // Explicitly set the names. Or else the names will have implicitly `io_` prefix
  io.inData.setName("_in") // NOTE: cannot set the name to `in` which is reserved
  io.outData.setName("_out") // NOTE: cannot set the name to `out` which is reserved
}

object HDLBitsVector2Verilog extends App {
  Config.spinal
    .copy(netlistFileName = "Vector2.v") // set the output file name
    .generateVerilog(HDLBitsVector2())
}
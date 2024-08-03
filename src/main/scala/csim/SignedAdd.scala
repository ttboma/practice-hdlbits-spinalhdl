package csim

import spinal.core._
import spinal.lib._
 
// Define a SpinalHDL component
class MySIntAdder extends Component {
  val io = new Bundle {
    val a = in SInt(8 bits)
    val b = in SInt(8 bits)
    val z = out SInt(8 bits)
    val overflow = out Bool()
  }
 
  // Perform the signed addition
  val result = io.a + io.b
 
  // Assign the result to the output, taking only the lower 8 bits
  io.z := result(7 downto 0)
 
  // Detect overflow
  val overflowDetected = (io.a.msb === io.b.msb) && (result.msb =/= io.a.msb)
  io.overflow := overflowDetected
}
 
// Generate the Verilog
object MySIntAdderVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new MySIntAdder)
  }
}
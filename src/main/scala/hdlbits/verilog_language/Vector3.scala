package hdlbits.verilog_language

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsVector3 extends App {
  Config.spinal("Vector3.v") // set the output file name
    .generateVerilog(HdlBitsVector3())
}

// https://hdlbits.01xz.net/wiki/Vector3
case class HdlBitsVector3() extends Component {
  val io = new Bundle {
    val input = Vec.fill(6)(in Bits (5 bits)) // NOTE: Use `Vec` instead of `Scala.List`
    val output = Vec.fill(4)(out Bits (8 bits))
  }

  // Concatenate inputs a to f with 2'b11
  val concatenated = Cat(Cat(io.input), B"11")

  for (i <- 0 to 3) {
    io.output(i) := concatenated(i * 8, 8 bits)
  }

  // Set the name of the generated module name
  setDefinitionName("top_module")

  // Set the generated input names
  List("f", "e", "d", "c", "b", "a").zipWithIndex.foreach { case (name, idx) =>
    io.input(idx).setName(name)
  }

  // Set the generated output names
  List("z", "y", "x", "w").zipWithIndex.foreach { case (name, idx) =>
    io.output(idx).setName(name)
  }
}

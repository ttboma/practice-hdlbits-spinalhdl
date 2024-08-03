package hdlbits.verilog_language

import spinal.core._
import spinal.core.sim._

// https://hdlbits.01xz.net/wiki/Vector5
case class HDLBitsVector5() extends Component {
  val io = new Bundle {
    val input = Vec.fill(5)(in Bool ())
    val output = out Bits (25 bits)
  }

  io.output := ~Cat((0 to 4).map(i => io.input(i) #* 5)) ^ (Cat(io.input) #* 5)
}

object HDLBitsVector5 {
  def apply(): HDLBitsVector5 = {
    val rtl = new HDLBitsVector5()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HDLBitsVector5): Unit = {
    // Set the name of the generated module name
    mod.setDefinitionName("top_module")

    // Set the generated input names
    List("e", "d", "c", "b", "a").zipWithIndex.foreach { case (name, idx) =>
      mod.io.input(idx).setName(name)
    }

    // Set the generated output names
    mod.io.output.setName(
      "_out"
    ) // NOTE: cannot set the name to `out` which is reserved
  }
}

object HDLBitsVector5Verilog extends App {
  Config.spinal
    .copy(netlistFileName = "Vector5.v") // set the output file name
    .generateVerilog(HDLBitsVector5())
}

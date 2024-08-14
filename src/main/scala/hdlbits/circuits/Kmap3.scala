package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsKmap3 extends App {
  Config
    .spinal("Kmap3.v") // set the output file name
    .generateVerilog(HdlBitsKmap3().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Kmap3
case class HdlBitsKmap3() extends Component {
  val io = new Bundle {
    val a, b, c, d = in Bool ()
    val cout =
      out Bool () // out is a reserved keyword in Scala, so we use cout instead
  }

  io.cout := io.a | ((~io.b) & io.c)

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

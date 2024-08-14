package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsKmap4 extends App {
  Config
    .spinal("Kmap4.v") // set the output file name
    .generateVerilog(HdlBitsKmap4().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Kmap4
case class HdlBitsKmap4() extends Component {
  val io = new Bundle {
    val a, b, c, d = in Bool ()
    val cout =
      out Bool () // out is a reserved keyword in Scala, so we use cout instead
  }

  io.cout :=
    !io.a & io.b & !io.c & !io.d |
      io.a & !io.b & !io.c & !io.d |
      !io.a & !io.b & !io.c & io.d |
      io.a & io.b & !io.c & io.d |
      !io.a & io.b & io.c & io.d |
      io.a & !io.b & io.c & io.d |
      !io.a & !io.b & io.c & !io.d |
      io.a & io.b & io.c & !io.d

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

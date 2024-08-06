package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFadd extends App {
  Config
    .spinal("Fadd.v") // set the output file name
    .generateVerilog(HdlBitsFadd().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Fadd
case class HdlBitsFadd() extends Component {
  val io = new Bundle {
    val a = in Bool ()
    val b = in Bool ()
    val cin = in Bool ()
    val sum = out Bool ()
    val cout = out Bool ()
  }

  io.sum := io.a ^ io.b ^ io.cin

  // This is functional correct, but the generated verilog is worse
  // io.sum := (io.a.asUInt + io.b.asUInt + io.c.asUInt).asBool

  io.cout := (io.a & io.b) | (io.b & io.cin) | (io.a & io.cin)

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

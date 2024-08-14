package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExams2012Q1g extends App {
  Config
    .spinal("Exams2012Q1g.v") // set the output file name
    .generateVerilog(HdlBitsExams2012Q1g().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2014_q3
case class HdlBitsExams2012Q1g() extends Component {
  val io = new Bundle {
    val c, d = in Bool ()
    val mux_in = out Bits (4 bits)
  }

  io.mux_in(0) := io.d | io.c
  io.mux_in(1) := False
  io.mux_in(2) := !io.d
  io.mux_in(3) := io.d & io.c

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

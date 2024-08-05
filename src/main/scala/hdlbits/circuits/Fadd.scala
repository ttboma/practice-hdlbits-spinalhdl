package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFadd extends App {
  Config
    .spinal("Fadd.v") // set the output file name
    .generateVerilog(HdlBitsFadd())
}

// https://hdlbits.01xz.net/wiki/Fadd
case class HdlBitsFadd() extends Component {
  val io = new Bundle {
    val a = in Bool ()
    val b = in Bool ()
    val c = in Bool ()
    val sum = out Bool ()
    val cout = out Bool ()
  }

  io.sum := io.a ^ io.b ^ io.c

  // This is functional correct, but the generated verilog is worse
  // io.sum := (io.a.asUInt + io.b.asUInt + io.c.asUInt).asBool

  io.cout := (io.a & io.b) | (io.b & io.c) | (io.a & io.c)
}

object HdlBitsFadd {
  def apply(): HdlBitsFadd = {
    val rtl = new HdlBitsFadd()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsFadd) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

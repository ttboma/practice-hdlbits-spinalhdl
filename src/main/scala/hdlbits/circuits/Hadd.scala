package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsHadd extends App {
  Config
    .spinal("Hadd.v") // set the output file name
    .generateVerilog(HdlBitsHadd())
}

// https://hdlbits.01xz.net/wiki/Hadd
case class HdlBitsHadd() extends Component {
  val io = new Bundle {
    val a = in Bool ()
    val b = in Bool ()
    val sum = out Bool ()
    val cout = out Bool ()
  }

  io.sum := (io.a.asUInt + io.b.asUInt).asBool
  io.cout := io.a & io.b
}

object HdlBitsHadd {
  def apply(): HdlBitsHadd = {
    val rtl = new HdlBitsHadd()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsHadd) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

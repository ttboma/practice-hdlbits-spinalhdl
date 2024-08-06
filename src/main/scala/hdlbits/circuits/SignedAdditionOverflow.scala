package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsSignedAdditionOverflow extends App {
  Config
    .spinal("SignedAdditionOverflow.v") // set the output file name
    .generateVerilog(HdlBitsSignedAdditionOverflow().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2014_q1c
case class HdlBitsSignedAdditionOverflow() extends Component {
  val io = new Bundle {
    val a = in SInt (8 bits)
    val b = in SInt (8 bits)
    val s = out SInt (8 bits)
    val overflow = out Bool ()
  }

  val sum = io.a + io.b
  io.s := sum
  io.overflow := (io.a.msb === io.b.msb) && (io.a.msb =/= sum.msb)

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsAdder3 extends App {
  Config
    .spinal("Adder3.v") // set the output file name
    .generateVerilog(HdlBitsAdder3().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Adder3
case class HdlBitsAdder3() extends Component {
  val io = new Bundle {
    val a = in UInt (3 bits)
    val b = in UInt (3 bits)
    val cin = in Bool ()
    val sum = out UInt (3 bits)
    val cout = out UInt (3 bits)
  }

  val fa = List.fill(3)(HdlBitsFadd().setNames("Fa"))

  fa(0).io.cin := io.cin
  fa(0).io.a := io.a(0)
  fa(0).io.b := io.b(0)
  io.sum(0) := fa(0).io.sum
  io.cout(0) := fa(0).io.cout

  for (i <- 1 until 3) {
    fa(i).io.cin := fa(i - 1).io.cout
    fa(i).io.a := io.a(i)
    fa(i).io.b := io.b(i)
    io.sum(i) := fa(i).io.sum
    io.cout(i) := fa(i).io.cout
  }

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

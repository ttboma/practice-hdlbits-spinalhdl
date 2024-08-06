package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsAdder100 extends App {
  Config
    .spinal("Adder100.v") // set the output file name
    .generateVerilog(HdlBitsAdder100(100).setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Adder100
case class HdlBitsAdder100(width: Int) extends Component {
  val io = new Bundle {
    val a = in UInt (width bits)
    val b = in UInt (width bits)
    val cin = in Bool ()
    val cout = out Bool ()
    val sum = out UInt (width bits)
  }

  val fa = List.fill(width)(HdlBitsFadd().setNames("Fa"))

  fa(0).io.cin := io.cin
  fa(0).io.a := io.a(0)
  fa(0).io.b := io.b(0)
  io.sum(0) := fa(0).io.sum

  for (i <- 1 until width) {
    fa(i).io.cin := fa(i - 1).io.cout
    fa(i).io.a := io.a(i)
    fa(i).io.b := io.b(i)
    io.sum(i) := fa(i).io.sum
  }

  io.cout := fa(width - 1).io.cout

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

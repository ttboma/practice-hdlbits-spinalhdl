package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsAdder extends App {
  Config
    .spinal("Adder.v") // set the output file name
    .generateVerilog(HdlBitsAdder(4).setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Exams/m2014_q4j
case class HdlBitsAdder(width: Int) extends Component {
  val io = new Bundle {
    val x = in UInt (width bits)
    val y = in UInt (width bits)
    val sum = out UInt (width + 1 bits)
  }

  val fa = List.fill(width)(HdlBitsFadd().setNames("Fa"))

  fa(0).io.cin := False
  fa(0).io.a := io.x(0)
  fa(0).io.b := io.y(0)
  io.sum(0) := fa(0).io.sum

  for (i <- 1 until width) {
    fa(i).io.cin := fa(i - 1).io.cout
    fa(i).io.a := io.x(i)
    fa(i).io.b := io.y(i)
    io.sum(i) := fa(i).io.sum
  }

  io.sum(width) := fa(width - 1).io.cout

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

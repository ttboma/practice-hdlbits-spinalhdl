package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsAdder3 extends App {
  Config
    .spinal("Adder3.v") // set the output file name
    .generateVerilog(HdlBitsAdder3())
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

  val fa = List.fill(3)(Fadd())

  fa(0).io.c := io.cin
  fa(0).io.a := io.a(0)
  fa(0).io.b := io.b(0)
  io.sum(0) := fa(0).io.sum
  io.cout(0) := fa(0).io.cout

  for (i <- 1 until 3) {
    fa(i).io.c := fa(i - 1).io.cout
    fa(i).io.a := io.a(i)
    fa(i).io.b := io.b(i)
    io.sum(i) := fa(i).io.sum
    io.cout(i) := fa(i).io.cout
  }
}

object HdlBitsAdder3 {
  def apply(): HdlBitsAdder3 = {
    val rtl = new HdlBitsAdder3()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsAdder3) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

case class Fadd() extends Component {
  val io = new Bundle {
    val a = in Bool ()
    val b = in Bool ()
    val c = in Bool ()
    val sum = out Bool ()
    val cout = out Bool ()
  }

  io.sum := io.a ^ io.b ^ io.c
  io.cout := (io.a & io.b) | (io.b & io.c) | (io.a & io.c)
}

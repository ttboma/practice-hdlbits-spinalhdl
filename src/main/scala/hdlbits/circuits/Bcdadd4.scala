package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsBcdadd4 extends App {
  Config
    .spinal("Bcdadd4.v") // set the output file name
    .generateVerilog(HdlBitsBcdadd4().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Bcdadd4
case class HdlBitsBcdadd4() extends Component {
  val io = new Bundle {
    val a, b = in UInt (16 bits)
    val cin = in Bool ()
    val cout = out Bool ()
    val sum = out UInt (16 bits)
  }

  val bcd_fa = List.fill(4)(HdlBitsBcdFadd().setNames("bcd_fadd"))

  bcd_fa(0).io.cin := io.cin
  bcd_fa(0).io.a := io.a(3 downto 0)
  bcd_fa(0).io.b := io.b(3 downto 0)
  io.sum(3 downto 0) := bcd_fa(0).io.sum

  for (i <- 1 until 4) {
    bcd_fa(i).io.cin := bcd_fa(i - 1).io.cout
    bcd_fa(i).io.a := io.a(4 * i + 3 downto i * 4)
    bcd_fa(i).io.b := io.b(4 * i + 3 downto i * 4)
    io.sum(4 * i + 3 downto i * 4) := bcd_fa(i).io.sum
  }

  io.cout := bcd_fa(3).io.cout

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

case class HdlBitsBcdFadd() extends Component {
  val io = new Bundle {
    val a = in UInt (4 bits) // 4-bit BCD input a
    val b = in UInt (4 bits) // 4-bit BCD input b
    val cin = in Bool () // Carry-in
    val sum = out UInt (4 bits) // 4-bit BCD output
    val cout = out Bool () // Carry-out
  }

  val fa = HdlBitsAdder(4)
  val binarySum = UInt(5 bits)

  fa.io.x := io.a
  fa.io.y := io.b
  binarySum := fa.io.sum

  // https://www.geeksforgeeks.org/bcd-adder-in-digital-logic/
  val correction = Mux(
    binarySum(4) | (binarySum(3) & binarySum(2)) | (binarySum(3) & binarySum(
      1
    )),
    U"0110",
    U"0000"
  )

  val fa2 = HdlBitsAdder(4)

  fa2.io.x := binarySum(3 downto 0)
  fa2.io.y := correction
  io.sum := fa2.io.sum(3 downto 0)
  io.cout := fa2.io.sum(4)

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

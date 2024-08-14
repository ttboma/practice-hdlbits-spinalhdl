package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

/// abcd  output
/// ----  ------
///    0    0
///    1    0
///   10    1
///   11
///  100    0
///  101    0
///  110    0
///  111    1
/// 1000
/// 1001    0
/// 1010    0
/// 1011
/// 1100
/// 1101    0
/// 1110    0
/// 1111    1

//    ab |00 01 11 10
// cd    |
// --------------------
// 00    | 0  0  x  x
// 01    | 0  0  0  0
// 11    | x  1  1  x
// 10    | 1  0  0  0

object VerilogHdlBitsExamsEce2412013Q2 extends App {
  Config
    .spinal("ExamsEce2412013Q2.v") // set the output file name
    .generateVerilog(HdlBitsExamsEce2412013Q2().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2013_q2
case class HdlBitsExamsEce2412013Q2() extends Component {
  val io = new Bundle {
    val a, b, c, d = in Bool ()
    val out_sop = out Bool ()
    val out_pos = out Bool ()
  }

  // SOP:
  // f = cd + a'b'c
  io.out_sop := (io.c & io.d) | (!io.a & !io.b & io.c)

  // POS:
  // f = (c' + cd'b + acd')''
  //   = c(c' + d + b')(a' + c' + d)
  io.out_pos := io.c & (!io.c | io.d | !io.b) & (!io.a | !io.c | io.d)

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

package csim

import spinal.core._
import spinal.core.sim._

object TestBoolTopVerilog extends App {
  def spinal = SpinalConfig(
    targetDirectory = "gen/csim"
  )

  def sim =
    SimConfig.withConfig(spinal).withFstWave.workspacePath("simWorkspace/csim")

  spinal
    .copy(netlistFileName = "TestBoolTop.v") // set the output file name
    .generateVerilog(TestBoolTop())
}

case class TestBoolTop() extends Component {
  val io = new Bundle {
    val a = in Bits (8 bits)
    val b = in Bits (9 bits)
    val c = in Bits (9 bits)
    val d = in Bits (9 bits)
    val x = in Bool()
    val e = out Bits (9 bits)
    val y = out UInt(8 bits)
  }

  io.y := io.a.asUInt + 1


  val logic = Ast(
    BitsBinaryOperator(
      _ & _,
      (a, b) => (a.resize(b.getWidth), b),
      BitsBinaryOperator(
        _ | _,
        (a, b) => (a.resize(b.getWidth), b),
        BitsOperand(io.a),
        BitsBinaryOperator(_ ^ _, (a, b) => (a.resize(b.getWidth), b), BitsOperand(io.c), BitsOperand(io.d))
      ),
      BitsBinaryOperator(_ ^ _, (a, b) => (a.resize(b.getWidth), b), BitsOperand(io.c), BitsOperand(io.d))
    )
  ).operate()

  io.e := logic
}

case class Ast(val root: ASTNode) {
    def operate(): Bits = root.drive()
}

// Base trait for AST nodes
sealed trait ASTNode {
  def drive(): Bits
}

// Case class for operands (e.g., variables, constants)
// =================================================================================================

case class BitsOperand(signal: Bits) extends ASTNode {
  def drive(): Bits = signal
}

// Case class for operators (e.g., addition, subtraction)
// =================================================================================================

case class BitsBinaryOperator(
    operator: (Bits, Bits) => Bits,
    width_strategy: (Bits, Bits) => (Bits, Bits),
    left: ASTNode,
    right: ASTNode
) extends ASTNode {
  def drive(): Bits = {
    val (a, b) = (left.drive(), right.drive())

    val (a2, b2) =
      if (a.getWidth < b.getWidth) {
        width_strategy(a, b)
      } else if (a.getWidth > b.getWidth) {
        width_strategy(b, a)
      } else {
        (a, b)
      }

    operator(a2, b2)
  }
}

case class BitsUnaryOperator(
    operator: (Bits) => Bits,
    operand: ASTNode,
) extends ASTNode {
  def drive(): Bits = {
    operator(operand.drive())
  }
}

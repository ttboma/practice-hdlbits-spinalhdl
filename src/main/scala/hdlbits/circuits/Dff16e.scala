package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsDff16e extends App {
  Config
    .spinal("Dff16e.v") // set the output file name
    .generateVerilog(HdlBitsDff16e().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Dff16e
case class HdlBitsDff16e() extends Component {
  val io = new Bundle {
    val clk, resetn = in Bool ()
    val byteena = in Bits (2 bits)
    val d = in Bits (16 bits)
    val q = out Bits (16 bits)
  }

  // Configure the clock domain
  val myClockDomain = ClockDomain(
    clock = io.clk,
    reset = io.resetn,
    config = ClockDomainConfig(
      clockEdge = RISING,
      resetKind = SYNC,
      resetActiveLevel = LOW
    )
  )

  // Define an Area which use myClockDomain
  val myArea = new ClockingArea(myClockDomain) {
    val qUpperReg = Reg(Bits(8 bits)) init (0)
    val qLowerReg = Reg(Bits(8 bits)) init (0)

    when(io.byteena(1)) {
      qUpperReg := io.d(15 downto 8)
    }
    when(io.byteena(0)) {
      qLowerReg := io.d(7 downto 0)
    }

    io.q := qUpperReg ## qLowerReg
  }

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

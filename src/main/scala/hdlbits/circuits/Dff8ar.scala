package hdlbits.circuits

import spinal.core._
import spinal.core.sim._

// https://hdlbits.01xz.net/wiki/Dff8ar
case class HDLBitsDff8ar() extends Component {
  val io = new Bundle {
    val clk = in Bool()
    val areset = in Bool() // active high asynchronous reset
    val d = in Bits (8 bits)
    val q = out Bits (8 bits)
  }

  // Configure the clock domain
  val myClockDomain = ClockDomain(
    clock  = io.clk,
    reset  = io.areset,
    config = ClockDomainConfig(
      clockEdge        = RISING,
      resetKind        = ASYNC,
      resetActiveLevel = HIGH 
    )
  )

  // Define an Area which use myClockDomain
  val myArea = new ClockingArea(myClockDomain) {
    val myReg = Reg(Bits(8 bits)) init(0)
    myReg := io.d
    io.q := myReg
  }
}

object HDLBitsDff8ar {
  def apply(): HDLBitsDff8ar = {
    val rtl = new HDLBitsDff8ar()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HDLBitsDff8ar) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

object HDLBitsDff8arVerilog extends App {
  Config.spinal
    .copy(netlistFileName = "Dff8ar.v") // set the output file name
    .generateVerilog(HDLBitsDff8ar())
}

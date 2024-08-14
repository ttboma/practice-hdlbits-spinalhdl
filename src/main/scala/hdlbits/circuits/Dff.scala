package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsDff extends App {
  Config
    .spinal("Dff.v") // set the output file name
    .generateVerilog(HdlBitsDff().setNames("top_module"))
}

// https://hdlbits.01xz.net/wiki/Dff
case class HdlBitsDff() extends Component {
  val io = new Bundle {
    val clk, d = in Bool ()
    val q = out Bool ()
  }

  // Define a clock domain without reset
  val MyClockDomain = ClockDomain(
    clock = io.clk,
    reset = null, // No reset signal
    config = ClockDomainConfig(
      resetKind = BOOT
    )
  )

  // Use the custom clock domain to create a flip-flop
  val MyArea = new ClockingArea(MyClockDomain) {
    val qReg = RegNext(io.d) // Register with no reset
    io.q := qReg
  }

  def setNames(top_module_name: String): this.type = {
    this.setDefinitionName(top_module_name)
    this.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
    this
  }
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsMux256to1v extends App {
  Config
    .spinal("Mux256to1v.v") // set the output file name
    .generateVerilog(HdlBitsMux256to1v())
}

// https://hdlbits.01xz.net/wiki/Mux256to1v
case class HdlBitsMux256to1v() extends Component {
  val io = new Bundle {
    val din = in Bits (1024 bits)
    val sel = in UInt (8 bits)
    val dout = out Bits (4 bits)
  }

  io.dout := io.sel.muxList(
    for (i <- 0 until 256) yield (i, io.din(i * 4 + 4 - 1 downto i * 4))
  )

  // A shorter way to do the same thing:
  // io.dout := io.din.subdivideIn(4 bits)(io.sel)
}

object HdlBitsMux256to1v {
  def apply(): HdlBitsMux256to1v = {
    val rtl = new HdlBitsMux256to1v()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsMux256to1v) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

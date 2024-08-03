package hdlbits.verilog_language

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBits7458 extends App {
  Config
    .spinal("7458.v")
    .generateVerilog(HdlBits7458())
}

// https://hdlbits.01xz.net/wiki/7458
case class HdlBits7458() extends Component {
  val io = new Bundle {
    val p1a = in Bool ()
    val p1b = in Bool ()
    val p1c = in Bool ()
    val p1d = in Bool ()
    val p1e = in Bool ()
    val p1f = in Bool ()
    val p1y = out Bool ()
    val p2a = in Bool ()
    val p2b = in Bool ()
    val p2c = in Bool ()
    val p2d = in Bool ()
    val p2y = out Bool ()
  }

  io.p1y := (io.p1a & io.p1c & io.p1b) | (io.p1f & io.p1e & io.p1d);
  io.p2y := (io.p2a & io.p2b) | (io.p2c & io.p2d);

  // Set the name of the generated module name
  setDefinitionName("top_module")

  // Explicitly set the names. Or else the names will have implicitly `io_` prefix
  io.elements.foreach { case (name, signal) =>
    signal.setName(name)
  }
}

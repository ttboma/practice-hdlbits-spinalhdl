package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsM2014Q4a extends App {
  Config
    .spinal("ExamsM2014Q4a.v") // set the output file name
    .generateVerilog(HdlBitsExamsM2014Q4a().setDefinitionName("top_module"))
}

// https://hdlbits.01xz.net/wiki/Exams/m2014_q4a
case class HdlBitsExamsM2014Q4a() extends Component {
  val io = new Bundle {
    val d, ena = in Bool ()
    val q = out Bool ()
  }

  // NOTE: Latches are often considered problematic in combinational logic design.
  //  SpinalHDL will check that no combinatorial signal will infer a latch in synthesis. In other words, that no combinatorial are partialy assigned.
  //  It is not possible to directly instantiate a latch in SpinalHDL (Maybe).
  //  So we use this opportunity to show how to instantiate a D Latch blackbox.

  // Instantiate the D Latch blackbox
  val dLatch = new HdlBitsDLatch()

  dLatch.io.D := io.d
  dLatch.io.enable := io.ena
  io.q := dLatch.io.Q

  // Remove io_ prefix
  noIoPrefix()
}

class HdlBitsDLatch extends BlackBox {
  val io = new Bundle {
    val D = in Bool ()
    val enable = in Bool ()
    val Q = out Bool ()
  }

  // Remove io_ prefix
  noIoPrefix()

  addRTLPath("ExamsM2014Q4a.v")
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExams2014Q3c extends App {
  Config
    .spinal("Exams2014Q3c.v") // set the output file name
    .generateVerilog(
      HdlBitsExams2014Q3c().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/2014_q3c
case class HdlBitsExams2014Q3c() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val y = in Bits (3 bits)
    val x = in Bool ()
    val Y0, z = out Bool ()
  }

  // Output logic
  io.Y0 := False
  io.z := False
  switch(io.y) {
    is(0) {
      io.Y0 := io.x
      io.z := False
    }
    is(1) {
      io.Y0 := ~io.x
      io.z := False
    }
    is(2) {
      io.Y0 := io.x
      io.z := False
    }
    is(3) {
      io.Y0 := ~io.x
      io.z := True
    }
    is(4) {
      io.Y0 := ~io.x
      io.z := True
    }
  }
}

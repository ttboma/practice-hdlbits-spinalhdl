package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsEce2412013Q4 extends App {
  Config
    .spinal("ExamsEce2412013Q4.v") // set the output file name
    .generateVerilog(
      HdlBitsExamsEce2412013Q4().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2013_q4
case class HdlBitsExamsEce2412013Q4() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val s = in UInt (3 bits)
    val fr3 = out Bool ()
    val fr2 = out Bool ()
    val fr1 = out Bool ()
    val dfr = out Bool ()
  }

  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      reset = io.reset,
      config = ClockDomainConfig(
        clockEdge = RISING,
        resetKind = SYNC,
        resetActiveLevel = HIGH
      )
    )
  ) {
    // Define states as constants
    val A = U"3'b111"
    val B = U"3'b011"
    val C = U"3'b001"
    val D = U"3'b000"

    // State registers with clock and reset
    val sState = Reg(UInt(3 bits)) init (D)
    val dfrState = Reg(Bool) init (True)

    // State transition logic
    sState := io.s
    when(io.s < sState) {
      dfrState := True
    } elsewhen (io.s > sState) {
      dfrState := False
    }

    // Output logic for fr1, fr2, fr3, and dfr
    io.fr1 := !sState(2)
    io.fr2 := !sState(1)
    io.fr3 := !sState(0)
    io.dfr := dfrState
  }
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExamsEce2412013Q7 extends App {
  Config
    .spinal("ExamsEce2412013Q7.v") // set the output file name
    .generateVerilog(
      HdlBitsExamsEce2412013Q7().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/ece241_2013_q7
case class HdlBitsExamsEce2412013Q7() extends Component {
  val io = new Bundle {
    val clk, j, k = in Bool ()
    val Q = out Bool ()
  }

  // Configure the clock domain
  val myClockDomain = ClockDomain(
    clock = io.clk,
    config = ClockDomainConfig(
      clockEdge = RISING
    )
  )

  // Define an Area which use myClockDomain
  val myArea = new ClockingArea(myClockDomain) {
    val QReg = Reg(Bool)

    when(io.j ^ io.k) {
      QReg := io.j
    } elsewhen (io.k) {
      QReg := ~QReg
    } otherwise {
      QReg := QReg
    }

    io.Q := QReg
  }
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsRotate100 extends App {
  Config
    .spinal("Rotate100.v") // set the output file name
    .generateVerilog(
      HdlBitsRotate100().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Rotate100
case class HdlBitsRotate100() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val load = in Bool ()
    val ena = in Bits (2 bits)
    val data = in UInt (100 bits)
    val q = out UInt (100 bits)
  }

  val c0 = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      config = ClockDomainConfig(
        clockEdge = RISING
      )
    )
  ) {
    val reg = Reg(UInt(100 bits))

    when(io.load) {
      reg := io.data
    } elsewhen (io.ena === 1) {
      reg := reg.rotateRight(1)
    } elsewhen (io.ena === 2) {
      reg := reg.rotateLeft(1)
    }
  }

  io.q := c0.reg
}

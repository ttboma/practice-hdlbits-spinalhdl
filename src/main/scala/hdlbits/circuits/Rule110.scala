package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsRule110 extends App {
  Config
    .spinal("Rule110.v") // set the output file name
    .generateVerilog(
      HdlBitsRule110()
        .noIoPrefix()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Rule110
case class HdlBitsRule110() extends Component {
  val io = new Bundle {
    val clk, load = in Bool ()
    val data = in Bits (512 bits)
    val q = out Bits (512 bits)
  }

  val qLeft, qRight = Bits(512 bits)

  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      config = ClockDomainConfig(
        clockEdge = RISING
      )
    )
  ) {
    val reg = Reg(Bits(512 bits))
    qLeft := False ## reg(511 downto 1)
    qRight := reg(510 downto 0) ## False

    when(io.load) {
      reg := io.data
    } otherwise {
      reg := (reg & ~qLeft) | (~reg & qRight) | (reg & ~qRight)
    }
  }

  io.q := clockRoot.reg
}

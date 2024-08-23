package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsMt2015Lfsr extends App {
  Config
    .spinal("Mt2015Lfsr.v") // set the output file name
    .generateVerilog(
      HdlBitsMt2015Lfsr()
        .noIoPrefix()
        .setSubmoduleClockName()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Mt2015_lfsr
case class HdlBitsMt2015Lfsr() extends Component {
  val io = new Bundle {
    val SW = in Bits (3 bits) // R
    val KEY = in Bits (2 bits) // L and clk
    val LEDR = out Bits (3 bits) // Q
  }

  val c0 = new ClockingArea(
    ClockDomain(
      clock = io.KEY(0),
      config = ClockDomainConfig(clockEdge = RISING)
    )
  ) {
    val reg = Array.fill(3)(Muxdff().noIoPrefix())

    for (i <- 0 until 2) {
      reg(i).io.L := io.KEY(1)
      reg(i).io.r_in := io.SW(i)
      reg(i).io.q_in := reg((i + 2) % 3).io.Q
      io.LEDR(i) := reg(i).io.Q
    }

    reg(2).io.L := io.KEY(1)
    reg(2).io.r_in := io.SW(2)
    reg(2).io.q_in := reg(1).io.Q ^ reg(2).io.Q
    io.LEDR(2) := reg(2).io.Q
  }

  def setSubmoduleClockName(name: String = "clk"): this.type = {
    c0.reg.foreach { _.clockDomain.clock.setName(name) }
    this
  }
}

// NOTE: Use the parent module's clock domain
case class Muxdff() extends Component {
  val io = new Bundle {
    val L, r_in, q_in = in Bool ()
    val Q = out Bool ()
  }
  val reg = Reg(Bool)
  reg := io.L.mux(io.r_in, io.q_in)

  io.Q := reg
}

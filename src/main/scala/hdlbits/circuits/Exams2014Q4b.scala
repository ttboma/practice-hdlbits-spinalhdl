package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsExams2014Q4b extends App {
  Config
    .spinal("Exams2014Q4b.v") // set the output file name
    .generateVerilog(
      HdlBitsExams2014Q4b()
        .noIoPrefix()
        .setSubmoduleClockName()
        .setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Exams/2014_q4b
case class HdlBitsExams2014Q4b() extends Component {
  val io = new Bundle {
    val SW = in Bits (4 bits)
    val KEY = in Bits (4 bits)
    val LEDR = out Bits (4 bits)
  }

  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.KEY(0),
      config = ClockDomainConfig(clockEdge = RISING)
    )
  ) {
    val reg = Array.fill(4)(Muxdff().noIoPrefix())

    for (i <- 0 until 3) {
      reg(i).io.r_in := io.SW(i)
      reg(i).io.q_in := io
        .KEY(1)
        .mux(
          reg(i + 1).io.Q,
          reg(i).io.Q
        )
      reg(i).io.L := io.KEY(2)
      io.LEDR(i) := reg(i).io.Q
    }

    reg(3).io.r_in := io.SW(3)
    reg(3).io.q_in := io
      .KEY(1)
      .mux(
        io.KEY(3),
        reg(3).io.Q
      )
    reg(3).io.L := io.KEY(2)
    io.LEDR(3) := reg(3).io.Q
  }

  def setSubmoduleClockName(name: String = "clk"): this.type = {
    clockRoot.reg.foreach { _.clockDomain.clock.setName(name) }
    this
  }
}

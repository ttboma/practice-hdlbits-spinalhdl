package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsCountbcd extends App {
  Config
    .spinal("Countbcd.v") // set the output file name
    .generateVerilog(HdlBitsCountbcd())
}

case class HdlBitsDecadeCount() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // active high synchronous reset
    val enable = in Bool ()
    val q = out UInt (4 bits)
  }

  // Define an Area which use custom clock domain
  val myArea = new ClockingArea(
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
    val qNext = Reg(UInt(4 bits)) init (U"4'd0")

    when(!io.enable) {} elsewhen (qNext < U"4'd9" && io.enable) {
      qNext := qNext + U"4'd1"
    } otherwise {
      qNext := U"4'd0"
    }
  }
  io.q := myArea.qNext
}

object HdlBitsDecadeCount {
  def apply(): HdlBitsDecadeCount = {
    val rtl = new HdlBitsDecadeCount()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsDecadeCount) {
    mod.setDefinitionName("decade_count")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

// https://hdlbits.01xz.net/wiki/Countbcd
case class HdlBitsCountbcd() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // active high asynchronous reset
    val ena = out Bits (3 bits)
    val q = out Bits (16 bits)
  }

  val enable = Vec.fill(4)(
    Bool()
  ) // Note: This has to be Vec. Got `COMBINATORIAL LOOP` if it is `Bits(4 bits)`
  val digits =
    List.fill(4)(HdlBitsDecadeCount()) // Cannot use `Vec`. Please use `List`

  for (i <- 0 to 3) {
    digits(i).io.clk := io.clk
    digits(i).io.reset := io.reset
    digits(i).io.enable := enable(i)
    io.q(i * 4, 4 bits) := digits(i).io.q.asBits
  }

  enable(0) := True
  enable(1) := (digits(0).io.q.asBits === 9)
  enable(2) := enable(1) && (digits(1).io.q.asBits === 9)
  enable(3) := enable(2) && (digits(2).io.q.asBits === 9)

  io.ena := Cat(enable)(3 downto 1)
}

object HdlBitsCountbcd {
  def apply(): HdlBitsCountbcd = {
    val rtl = new HdlBitsCountbcd()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsCountbcd) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsCountClock extends App {
  Config.spinal("CountClock.v") // set the output file name
    .generateVerilog(HdlBitsCountClock())
}

// https://hdlbits.01xz.net/wiki/Count_clock
case class HdlBitsCountClock() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // active high synchronous reset
    val ena = in Bool ()
    val pm = out Bool ()
    val hh = out Bits (8 bits)
    val mm = out Bits (8 bits)
    val ss = out Bits (8 bits)
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
    val pm = Reg(Bool) init (False)
    val hh1 = Reg(UInt(4 bits)) init (U"4'd1")
    val hh0 = Reg(UInt(4 bits)) init (U"4'd2")
    val mm1 = Reg(UInt(4 bits)) init (U"4'd0")
    val mm0 = Reg(UInt(4 bits)) init (U"4'd0")
    val ss1 = Reg(UInt(4 bits)) init (U"4'd0")
    val ss0 = Reg(UInt(4 bits)) init (U"4'd0")

    when(!io.ena) {} elsewhen (ss0 < U"4'd9") {
      ss0 := ss0 + U"4'd1"
    } elsewhen (ss1 < U"4'd5") {
      ss0 := U"4'd0"
      ss1 := ss1 + U"4'd1"
    } elsewhen (mm0 < U"4'd9") {
      ss0 := U"4'd0"
      ss1 := U"4'd0"
      mm0 := mm0 + U"4'd1"
    } elsewhen (mm1 < U"4'd5") {
      ss0 := U"4'd0"
      ss1 := U"4'd0"
      mm0 := U"4'd0"
      mm1 := mm1 + U"4'd1"
    } elsewhen (hh1 === U"4'd1" && hh0 === U"4'd2") {
      ss0 := U"4'd0"
      ss1 := U"4'd0"
      mm0 := U"4'd0"
      mm1 := U"4'd0"
      hh0 := U"4'd1"
      hh1 := U"4'd0"
    } elsewhen (hh1 === U"4'd1" && hh0 === U"4'd1") {
      ss0 := U"4'd0"
      ss1 := U"4'd0"
      mm0 := U"4'd0"
      mm1 := U"4'd0"
      hh0 := hh0 + U"4'd1"
      pm := ~pm
    } elsewhen (hh0 < U"4'd9") {
      ss0 := U"4'd0"
      ss1 := U"4'd0"
      mm0 := U"4'd0"
      mm1 := U"4'd0"
      hh0 := hh0 + U"4'd1"
    } otherwise {
      ss0 := U"4'd0"
      ss1 := U"4'd0"
      mm0 := U"4'd0"
      mm1 := U"4'd0"
      hh0 := U"4'd0"
      hh1 := hh1 + U"4'd1"
    }
  }

  io.pm := myArea.pm
  io.hh := myArea.hh1 ## myArea.hh0
  io.mm := myArea.mm1 ## myArea.mm0
  io.ss := myArea.ss1 ## myArea.ss0
}

object HdlBitsCountClock {
  def apply(): HdlBitsCountClock = {
    val rtl = new HdlBitsCountClock()
    setNames(rtl)
    rtl
  }

  private def setNames(mod: HdlBitsCountClock) {
    mod.setDefinitionName("top_module")
    mod.io.elements.foreach { case (name, signal) =>
      signal.setName(name)
    }
  }
}

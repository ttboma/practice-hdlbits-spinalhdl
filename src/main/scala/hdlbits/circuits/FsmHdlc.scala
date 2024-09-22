package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsmHdlc extends App {
  Config
    .spinal("FsmHdlc.v") // set the output file name
    .generateVerilog(
      HdlBitsFsmHdlc().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsFsmHdlcState extends SpinalEnum {
  val data, disc, flag, err = newElement()
}

import HdlBitsFsmHdlcState._

// https://hdlbits.01xz.net/wiki/Fsm_hdlc
case class HdlBitsFsmHdlc() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val din = in Bool () // 'in' is a reserved keyword, so we use 'din' instead
    val disc, flag, err = out Bool ()
  }

  // State register with clock and reset
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
    val state = RegInit(data)
    val counter = Reg(UInt(3 bits)) init (0)
  }

  // State transition logic
  clockRoot.counter := Mux(io.din, clockRoot.counter + 1, U(0, 3 bits))
  switch(clockRoot.state) {
    is(data) {
      when(clockRoot.counter === 5) {
        clockRoot.state := Mux(io.din, clockRoot.state, disc)
      } elsewhen (clockRoot.counter === 6) {
        clockRoot.state := Mux(io.din, err, flag)
      }
    }
    is(disc) {
      clockRoot.state := data
    }
    is(flag) {
      clockRoot.state := data
    }
    is(err) {
      clockRoot.state := Mux(io.din, clockRoot.state, data)
    }
  }

  // Output logic
  io.disc := clockRoot.state === disc
  io.flag := clockRoot.state === flag
  io.err := clockRoot.state === err
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsmSerialdata extends App {
  Config
    .spinal("FsmSerialdata.v") // set the output file name
    .generateVerilog(
      HdlBitsFsmSerialdata().noIoPrefix().setDefinitionName("top_module")
    )
}

import HdlBitsFsmSerialState._

// https://hdlbits.01xz.net/wiki/Fsm_serialdata
case class HdlBitsFsmSerialdata() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset to state A
    val din = in Bool () // 'in' is a reserved keyword, so we use 'din' instead
    val out_byte = out Bits (8 bits)
    val done =
      out Bool ()
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
    // State register with clock and reset
    val state = RegInit(idle)
    val counter = Reg(UInt(3 bits)) init (0)
    val outByte = Reg(Bits(8 bits)) init (0)

    // State transition logic
    switch(state) {
      is(idle) {
        when(!io.din) {
          state := start
        }
        counter := 0
      }
      is(start) {
        when(counter === 7) {
          state := stop
        } otherwise {
          counter := counter + 1
        }
        outByte(counter) := io.din
      }
      is(stop) { // stop-7
        when(io.din) {
          state := idle // idle-7 or idle-0
        } otherwise {
          counter := 0 // stop-0
        }
      }
    }

    // Output logic
    io.done := state === idle && counter === 7
    io.out_byte := outByte
  }
}

package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsmSerial extends App {
  Config
    .spinal("FsmSerial.v") // set the output file name
    .generateVerilog(
      HdlBitsFsmSerial().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsFsmSerialState extends SpinalEnum {
  val idle, start, stop = newElement()
}

import HdlBitsFsmSerialState._

// https://hdlbits.01xz.net/wiki/Fsm_serial
case class HdlBitsFsmSerial() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val din =
      in Bool () // `in` is a reserved keyword in Scala, so we use `din` instead
    val done = out Bool ()
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
  }
}

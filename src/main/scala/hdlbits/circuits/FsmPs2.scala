package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsmPs2 extends App {
  Config
    .spinal("FsmPs2.v") // set the output file name
    .generateVerilog(
      HdlBitsFsmPs2().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsFsmPs2State extends SpinalEnum {
  val byte1, byte2, byte3, idle = newElement()
}

import HdlBitsFsmPs2State._

// https://hdlbits.01xz.net/wiki/Fsm_ps2
case class HdlBitsFsmPs2() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val cin =
      in Bits (8 bits) // `in` is a reserved keyword, so we use `cin` instead
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
    // Define state parameters
    val nextState = HdlBitsFsmPs2State()

    // State register with clock and reset
    val state = RegInit(idle)

    // State transition logic (combinational)
    nextState := idle // NOTE: This is a default value, missing which will cause a compile error
    switch(state) {
      is(idle) {
        when(io.cin(3)) {
          nextState := byte1
        }
      }
      is(byte1) {
        nextState := byte2
      }
      is(byte2) {
        nextState := byte3
      }
      is(byte3) {
        when(io.cin(3)) {
          nextState := byte1
        }
      }
    }

    // State flip-flops (sequential)
    state := nextState

    // Output logic
    io.done := state === byte3
  }
}

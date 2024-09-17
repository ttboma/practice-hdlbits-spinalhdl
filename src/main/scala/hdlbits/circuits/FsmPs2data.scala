package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsmPs2data extends App {
  Config
    .spinal("FsmPs2data.v") // set the output file name
    .generateVerilog(
      HdlBitsFsmPs2data().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsFsmPs2dataState extends SpinalEnum {
  val idle, byte1, byte2, byte3 = newElement()
}

import HdlBitsFsmPs2dataState._

// https://hdlbits.01xz.net/wiki/Fsm_ps2data
case class HdlBitsFsmPs2data() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val cin =
      in Bits (8 bits) // `in` is a reserved keyword, so we use `cin` instead
    val out_bytes = out Bits (24 bits)
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
    val nextState = HdlBitsFsmPs2dataState()

    // State register with clock and reset
    val state = RegInit(idle)
    val dataPath = Reg(Bits(24 bits)) init (0)

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
    dataPath := dataPath |<< 8 | io.cin.resize(24 bits)

    // Output logic
    io.done := state === byte3
    io.out_bytes := dataPath
  }

}

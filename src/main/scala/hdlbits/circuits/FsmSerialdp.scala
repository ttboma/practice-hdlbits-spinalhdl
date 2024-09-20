package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsFsmSerialdp extends App {
  Config
    .spinal("FsmSerialdp.v") // set the output file name
    .generateVerilog(
      HdlBitsFsmSerialdp().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsFsmSerialdpState extends SpinalEnum {
  val idle, start, parity, stop = newElement()
}

import HdlBitsFsmSerialdpState._

// https://hdlbits.01xz.net/wiki/Fsm_serialdp
case class HdlBitsFsmSerialdp() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val reset = in Bool () // Synchronous reset
    val din = in Bool ()
    val out_byte = out Bits (8 bits)
    val done = out Bool ()
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
    val state = RegInit(idle)
    val counter = Reg(UInt(3 bits)) init (0)
    val outByte = Reg(Bits(8 bits)) init (0)
  }

  // Instantiate parityState inside a new ClockingArea using the custom clock domain
  val parityStateArea = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      reset = (clockRoot.state === idle && !io.din) || io.reset,
      config = ClockDomainConfig(
        clockEdge = RISING,
        resetKind = SYNC,
        resetActiveLevel = HIGH
      )
    )
  ) {
    val parityState = Parity().setNames().setDefinitionName("parity")
  }

  import clockRoot._
  import parityStateArea.parityState

  parityState.io.din := io.din

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
        state := parity
      } otherwise {
        counter := counter + 1
      }
      outByte(counter) := io.din
    }
    is(parity) {
      state := stop
    }
    is(stop) {
      when(io.din) {
        state := idle
      } otherwise {
        counter := 0
      }
    }
  }

  // State update
  // The last state `stop` needs `in` to be high, which have to included in
  // the parity check, so odd need to be initialized to true
  io.done := state === idle && counter === 7 && ~parityState.io.odd
  io.out_byte := outByte
}

case class Parity() extends Component {
  val io = new Bundle {
    val din = in Bool ()
    val odd = out Bool ()
  }

  val odd = Reg(Bool) init (False)
  when(io.din) {
    odd := ~odd
  }

  io.odd := odd

  def setNames(): this.type = {
    this.clockDomain.clock.setName("clk")
    this.clockDomain.reset.setName(
      "rset"
    ) // `reset` is a reserved keyword in Scala, so we use `rset` instead
    this.io.din.setName(
      "din"
    ) // `in` is a reserved keyword in Scala, so we use `din` instead
    this.io.odd.setName("odd")
    this
  }
}

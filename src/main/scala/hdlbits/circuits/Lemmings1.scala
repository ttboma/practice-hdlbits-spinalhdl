package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsLemmings1 extends App {
  Config
    .spinal("Lemmings1.v") // set the output file name
    .generateVerilog(
      HdlBitsLemmings1().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Lemmings1
case class HdlBitsLemmings1() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val areset = in Bool () // Freshly brainwashed Lemmings walk left.
    val bump_left, bump_right = in Bool ()
    val walk_left, walk_right = out Bool ()
  }

  val clockRoot = new ClockingArea(
    ClockDomain(
      clock = io.clk,
      reset = io.areset,
      config = ClockDomainConfig(
        clockEdge = RISING,
        resetKind = ASYNC,
        resetActiveLevel = HIGH
      )
    )
  ) {
    // State registers with clock and reset
    val walkDirection = Reg(Bool) init (False)

    // State transition logic
    when(io.bump_left & io.bump_right) {
      walkDirection := ~walkDirection
    } elsewhen (io.bump_right) {
      walkDirection := False
    } elsewhen (io.bump_left) {
      walkDirection := True
    }

    // Output logic for fr1, fr2, fr3, and dfr
    io.walk_left := ~walkDirection
    io.walk_right := walkDirection
  }
}

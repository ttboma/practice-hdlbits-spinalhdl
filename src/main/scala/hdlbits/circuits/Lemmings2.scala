package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsLemmings2 extends App {
  Config
    .spinal("Lemmings2.v") // set the output file name
    .generateVerilog(
      HdlBitsLemmings2().noIoPrefix().setDefinitionName("top_module")
    )
}

// https://hdlbits.01xz.net/wiki/Lemmings2
case class HdlBitsLemmings2() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val areset = in Bool () // Freshly brainwashed Lemmings walk left.
    val bump_left, bump_right, ground = in Bool ()
    val walk_left, walk_right, aaah = out Bool ()
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
    val groundState = Reg(Bool) init (True)

    // State transition logic
    groundState := io.ground

    when(
      !io.ground || !groundState
    ) {} elsewhen (io.bump_left & io.bump_right) {
      walkDirection := ~walkDirection
    } elsewhen (io.bump_right) {
      walkDirection := False
    } elsewhen (io.bump_left) {
      walkDirection := True
    }

    // Output logic
    io.walk_left := Mux(groundState, ~walkDirection, False)
    io.walk_right := Mux(groundState, walkDirection, False)
    io.aaah := ~groundState
  }
}

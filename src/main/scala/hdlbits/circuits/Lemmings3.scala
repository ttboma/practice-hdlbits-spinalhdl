package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsLemmings3 extends App {
  Config
    .spinal("Lemmings3.v") // set the output file name
    .generateVerilog(
      HdlBitsLemmings3().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsLemmings3State extends SpinalEnum {
  val walkingLeft, walkingRight, fallingLeft, fallingRight, diggingLeft,
      diggingRight = newElement()
}

// Import the enumeration to have visibility of its elements
import HdlBitsLemmings3State._

// https://hdlbits.01xz.net/wiki/Lemmings3
case class HdlBitsLemmings3() extends Component {
  val io = new Bundle {
    val clk = in Bool ()
    val areset = in Bool () // Freshly brainwashed Lemmings walk left.
    val bump_left, bump_right, ground, dig = in Bool ()
    val walk_left, walk_right, aaah, digging = out Bool ()
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
    // State register with clock and reset
    val state = RegInit(walkingLeft)

    // State transition logic
    switch(state) {
      is(walkingLeft) {
        when(!io.ground) {
          state := fallingLeft
        } elsewhen (io.dig) {
          state := diggingLeft
        } elsewhen (io.bump_left) {
          state := walkingRight
        }
      }
      is(walkingRight) {
        when(!io.ground) {
          state := fallingRight
        } elsewhen (io.dig) {
          state := diggingRight
        } elsewhen (io.bump_right) {
          state := walkingLeft
        }
      }
      is(fallingLeft) {
        when(io.ground) {
          state := walkingLeft
        }
      }
      is(fallingRight) {
        when(io.ground) {
          state := walkingRight
        }
      }
      is(diggingLeft) {
        when(!io.ground) {
          state := fallingLeft
        }
      }
      is(diggingRight) {
        when(!io.ground) {
          state := fallingRight
        }
      }
    }

    // Output logic
    io.walk_left := state === walkingLeft
    io.walk_right := state === walkingRight
    io.aaah := state === fallingLeft || state === fallingRight
    io.digging := state === diggingLeft || state === diggingRight
  }
}

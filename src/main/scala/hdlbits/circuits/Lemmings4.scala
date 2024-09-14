package hdlbits.circuits

import spinal.core._
import spinal.core.sim._
import hdlbits.Config

object VerilogHdlBitsLemmings4 extends App {
  Config
    .spinal("Lemmings4.v") // set the output file name
    .generateVerilog(
      HdlBitsLemmings4().noIoPrefix().setDefinitionName("top_module")
    )
}

object HdlBitsLemmings4State extends SpinalEnum {
  val walkingLeft, walkingRight, fallingLeft, fallingRight, diggingLeft,
      diggingRight, splat, dead = newElement()
}

// Import the enumeration to have visibility of its elements
import HdlBitsLemmings4State._

// https://hdlbits.01xz.net/wiki/Lemmings4
case class HdlBitsLemmings4() extends Component {
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
    // Define state parameters
    val nextState = HdlBitsLemmings4State()

    // State register with clock and reset
    val state = RegInit(walkingLeft)
    val counter = Reg(UInt(5 bits)) init (1)

    // State transition logic
    switch(state) {
      is(walkingLeft) {
        when(!io.ground) {
          nextState := fallingLeft
        } elsewhen (io.dig) {
          nextState := diggingLeft
        } elsewhen (io.bump_left) {
          nextState := walkingRight
        } otherwise {
          nextState := walkingLeft
        }
      }
      is(walkingRight) {
        when(!io.ground) {
          nextState := fallingRight
        } elsewhen (io.dig) {
          nextState := diggingRight
        } elsewhen (io.bump_right) {
          nextState := walkingLeft
        } otherwise {
          nextState := walkingRight
        }
      }
      is(fallingLeft) {
        when(!io.ground & counter <= 20) {
          nextState := fallingLeft
        } elsewhen (!io.ground & counter > 20) {
          nextState := splat
        } otherwise {
          nextState := walkingLeft
        }
      }
      is(fallingRight) {
        when(!io.ground & counter <= 20) {
          nextState := fallingRight
        } elsewhen (!io.ground & counter > 20) {
          nextState := splat
        } otherwise {
          nextState := walkingRight
        }
      }
      is(diggingLeft) {
        when(!io.ground) {
          nextState := fallingLeft
        } otherwise {
          nextState := diggingLeft
        }
      }
      is(diggingRight) {
        when(!io.ground) {
          nextState := fallingRight
        } otherwise {
          nextState := diggingRight
        }
      }
      is(splat) {
        when(io.ground) {
          nextState := dead
        } otherwise {
          nextState := splat
        }
      }
      is(dead) {
        nextState := dead
      }
    }

    // State flip-flops with asynchronous reset
    state := nextState
    when(nextState === fallingLeft | nextState === fallingRight) {
      counter := counter + 1
    } otherwise {
      counter := 1
    }

    // Output logic
    io.walk_left := state === walkingLeft
    io.walk_right := state === walkingRight
    io.aaah := state === fallingLeft | state === fallingRight | state === splat
    io.digging := state === diggingLeft | state === diggingRight
  }
}

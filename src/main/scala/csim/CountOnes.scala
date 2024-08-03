package csim

import spinal.core._
import spinal.core.sim._

object CountOnesVerilog extends App {
  def spinal = SpinalConfig(
    targetDirectory = "gen/csim"
  )

  def sim =
    SimConfig.withConfig(spinal).withFstWave.workspacePath("simWorkspace/csim")

  spinal
    .copy(netlistFileName = "CountOnes.v") // set the output file name
    .generateVerilog(CountOnes())
}

// Hardware definition
case class CountOnes() extends Component {
  val io = new Bundle {
    val a = in UInt (16 bits)
    val b = in UInt (16 bits)
    val c = out UInt (16 bits)
  }

  io.c := io.a + io.b
}

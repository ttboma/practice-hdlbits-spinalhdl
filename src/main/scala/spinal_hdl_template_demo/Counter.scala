package spinal_hdl_template_demo

import spinal.core._
import spinal.core.sim._

class Counter extends Component {
  val counter = out(Reg(UInt(8 bits)) init (0))
  counter := counter + 1
}

object TestCounter extends App {
  val spinalConfig =
    SpinalConfig(defaultClockDomainFrequency = FixedFrequency(10 MHz))

  val compiled = SimConfig
    .withConfig(spinalConfig)
    .withWave
    .allOptimisation
    .workspacePath("simWorkspace/spinal_hdl_template_demo")
    .compile(new Counter)

  compiled
    .doSim("testA") { dut =>
      SimTimeout(1000)
      dut.clockDomain.forkStimulus(10)
      dut.clockDomain.waitSamplingWhere(dut.counter.toInt == 20)
      println("done")
    }

  compiled
    .doSim("testB") { dut =>
      SimTimeout(1000)
      dut.clockDomain.forkStimulus(10)
      fork {
        dut.clockDomain.waitSamplingWhere(dut.counter.toInt == 20)
        println("done")
        simSuccess()
      }
      simThread.suspend() // Avoid the "doSim" completion
    }
}

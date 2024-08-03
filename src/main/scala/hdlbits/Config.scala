package hdlbits

import spinal.core._
import spinal.core.sim._

object Config {
  def spinal(targetDirectory: String) = SpinalConfig(
    targetDirectory = "gen/hdlbits/",
    netlistFileName = targetDirectory,
    defaultConfigForClockDomains = ClockDomainConfig(
      resetActiveLevel = HIGH
    ),
    onlyStdLogicVectorAtTopLevelIo = true
  )

  def sim(targetDirectory: String) = SimConfig
    .withConfig(
      SpinalConfig(
        targetDirectory = "gen/hdlbits/",
        netlistFileName = targetDirectory,
        defaultConfigForClockDomains = ClockDomainConfig(
          resetActiveLevel = HIGH
        ),
        onlyStdLogicVectorAtTopLevelIo = true
      )
    )
    .withFstWave
    .workspacePath("simWorkspace/hdlbits" + targetDirectory)
}

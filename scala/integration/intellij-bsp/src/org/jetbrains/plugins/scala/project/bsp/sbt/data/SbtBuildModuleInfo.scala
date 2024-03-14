package org.jetbrains.plugins.scala.project.bsp.sbt.data

import ch.epfl.scala.bsp4j.{BuildTargetIdentifier, SbtBuildTarget}

object SbtBuildModuleInfo {
  var sbtBuildTarget: Option[SbtBuildTarget] = None
  var sbtBuildTargetId: Option[BuildTargetIdentifier] = None
}

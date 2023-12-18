package org.jetbrains.plugins.scala.bsp.config

import com.intellij.openapi.externalSystem.model.ProjectSystemId
import org.jetbrains.plugins.bsp.extension.points.BuildToolId

object ScalaPluginConstants {
  val ID = "scalabsp"
  val BUILD_TOOL_ID: BuildToolId = new BuildToolId("Sbt")
  val SYSTEM_ID = new ProjectSystemId(ID, "Scala BSP")
  val SUPPORTED_CONFIG_FILE_EXTENSIONS: List[String] = List("sbt")
  val SUPPORTED_CONFIG_FILE_NAMES: List[String] = List("build", "")
}

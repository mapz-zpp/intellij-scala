package org.jetbrains.plugins.scala.bsp.flow.open

import org.jetbrains.plugins.bsp.flow.open.{BspProjectOpenProcessorExtension, BuildToolId}
import org.jetbrains.plugins.scala.bsp.BspFeatureFlags
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants

class ScalaBspProjectOpenProcessor extends BspProjectOpenProcessorExtension {

  override def getShouldBspProjectOpenProcessorBeAvailable: Boolean = BspFeatureFlags.isBspPluginIntegrationEnabled

  override def getBuildToolId: BuildToolId = new BuildToolId("sbt")
}

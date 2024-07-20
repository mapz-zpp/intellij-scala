package org.jetbrains.plugins.scala.bsp.flow.open

import org.jetbrains.plugins.bsp.extension.points.BuildToolId
import org.jetbrains.plugins.bsp.flow.open.BspProjectOpenProcessorExtension
import org.jetbrains.plugins.scala.bsp.config.MillScalaPluginConstants

class MillScalaBspProjectOpenProcessorExtension extends BspProjectOpenProcessorExtension {
  override def getShouldBspProjectOpenProcessorBeAvailable: Boolean = true

  override def getBuildToolId: BuildToolId = MillScalaPluginConstants.BUILD_TOOL_ID
}

package org.jetbrains.plugins.scala.bsp.assets

import com.intellij.openapi.util.IconLoader
import org.jetbrains.plugins.bsp.assets.BuildToolAssetsExtension
import org.jetbrains.plugins.bsp.extension.points.BuildToolId
import org.jetbrains.plugins.scala.bsp.config.MillScalaPluginConstants

import javax.swing.Icon

class MillAssetsExtension extends BuildToolAssetsExtension {
  override def getIcon: Icon = IconLoader.getIcon("icons/buildServerProtocol.svg", classOf[MillAssetsExtension])

  override def getInvalidTargetIcon: Icon = IconLoader.getIcon("icons/buildServerProtocolTarget_red.svg", classOf[MillAssetsExtension])

  override def getPresentableName: String = "mill-bsp"

  override def getBuildToolId: BuildToolId = MillScalaPluginConstants.BUILD_TOOL_ID

  override def getTargetIcon: Icon = IconLoader.getIcon("icons/buildServerProtocolTarget.svg", classOf[MillAssetsExtension])
}
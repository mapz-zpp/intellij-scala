package org.jetbrains.plugins.scala.bsp.assets

import com.intellij.openapi.util.IconLoader
import org.jetbrains.plugins.bsp.assets.BuildToolAssetsExtension
import org.jetbrains.plugins.bsp.extension.points.BuildToolId
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants

import javax.swing.Icon

class SbtAssetsExtension extends BuildToolAssetsExtension {
  override def getIcon: Icon = IconLoader.getIcon("icons/buildServerProtocol.svg", classOf[SbtAssetsExtension])

  override def getLoadedTargetIcon: Icon = IconLoader.getIcon("icons/buildServerProtocolTarget.svg", classOf[SbtAssetsExtension])

  override def getInvalidTargetIcon: Icon = IconLoader.getIcon("icons/buildServerProtocolTarget_red.svg", classOf[SbtAssetsExtension])

  override def getUnloadedTargetIcon: Icon = IconLoader.getIcon("icons/buildServerProtocolTarget_grey.svg", classOf[SbtAssetsExtension])

  override def getPresentableName: String = "sbt-bsp"

  override def getBuildToolId: BuildToolId = ScalaPluginConstants.BUILD_TOOL_ID
}

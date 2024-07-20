package org.jetbrains.plugins.scala.bsp.extension

import org.jetbrains.plugins.bsp.extension.points.{BuildTargetClassifierExtension, BuildToolId}
import org.jetbrains.plugins.bsp.magicmetamodel.impl.workspacemodel.BuildTargetInfo
import org.jetbrains.plugins.scala.bsp.config.MillScalaPluginConstants

import java.io.File
import java.util

class MillScalaBuildTargetClassifier extends BuildTargetClassifierExtension {
  override def getSeparator: String = "."
  override val getBuildToolId: BuildToolId = MillScalaPluginConstants.BUILD_TOOL_ID

  override def calculateBuildTargetName(buildTargetInfo: BuildTargetInfo): String = {
    val splitPath = buildTargetInfo.getId.split(File.separator)
//    val (_, targetSegment) = splitPath.span(segment => !segment.startsWith("#"))
//
//    if (targetSegment.length == 1 && targetSegment.head.endsWith("-build")) {
//      "Build"
//    } else if (targetSegment.length == 2) {
//      targetSegment(1)
//    } else {
//      throw new RuntimeException(f"Can't parse buildTargetInfoId = ${buildTargetInfo.getId}")
//    }
    splitPath.last
  }

  override def calculateBuildTargetPath(buildTargetInfo: BuildTargetInfo): util.List[String] = {
    val displayName = buildTargetInfo.getDisplayName
    val formattedDisplayName = if (displayName.endsWith("-test")) {
      displayName.stripSuffix("-test")
    } else if (displayName.endsWith("-build")) {
      displayName.stripSuffix("-build")
    } else {
      displayName
    }

    util.List.of(formattedDisplayName)
  }

}

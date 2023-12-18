package org.jetbrains.plugins.scala.bsp.extension

import org.jetbrains.plugins.bsp.extension.points.{BuildTargetClassifierExtension, BuildToolId}
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants

import java.util
import scala.jdk.CollectionConverters._

private class ScalaBuildTargetClassifier extends BuildTargetClassifierExtension {
  override val getBuildToolId: BuildToolId = ScalaPluginConstants.BUILD_TOOL_ID
  override val getSeparator = "/"

  override def calculateBuildTargetPath(s: String): util.List[String] = {
    if (s != null) {
      List.apply("abc").asJava
    } else {
      List.apply("bef").asJava
    }
  }

  override def calculateBuildTargetName(s: String): String =
    if (s != null) {
      "noa"
    } else {
      "bea"
    }
}

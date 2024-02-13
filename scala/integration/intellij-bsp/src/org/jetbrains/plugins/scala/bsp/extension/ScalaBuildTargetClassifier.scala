package org.jetbrains.plugins.scala.bsp.extension

import com.intellij.ide.impl.ProjectUtil
import org.jetbrains.plugins.bsp.extension.points.{BuildTargetClassifierExtension, BuildToolId}
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants

import java.io.File
import java.util
import scala.jdk.CollectionConverters._

private class ScalaBuildTargetClassifier extends BuildTargetClassifierExtension {
  override val getBuildToolId: BuildToolId = ScalaPluginConstants.BUILD_TOOL_ID
  override val getSeparator = "/"

  private def getPathSegments(path: String): Option[Array[String]] = {
    Option(path.stripPrefix("file:").split(File.separator).filter(_.nonEmpty))
  }

  private def getBuildTargetWithStrippedPath(buildTarget: String): Array[String] = {
    val maybeSplitBuildTargetPath = getPathSegments(buildTarget)
    if (maybeSplitBuildTargetPath.isEmpty) {
      throw new RuntimeException(s"Split buildTarget path cannot be empty. buildTarget = [$buildTarget]")
    }

    val splitBuildTargetPath = maybeSplitBuildTargetPath.get

    Option(ProjectUtil.getActiveProject)
      .flatMap(currentProject => getPathSegments(currentProject.getBasePath))
      .filter(projectPathSegments => projectPathSegments.nonEmpty)
      .map(projectPathSegments => splitBuildTargetPath.drop(projectPathSegments.length - 1))
      .getOrElse(splitBuildTargetPath)
  }

  override def calculateBuildTargetPath(buildTarget: String): util.List[String] = {
    val buildTargetSegments = getBuildTargetWithStrippedPath(buildTarget)
    val (nonTargetSegments, _) = buildTargetSegments.span(!_.startsWith("#"))

    nonTargetSegments.toList.asJava
  }

  override def calculateBuildTargetName(buildTarget: String): String = {
    val buildTargetSegments = getBuildTargetWithStrippedPath(buildTarget)
    val (_, targetSegments) = buildTargetSegments.span(!_.startsWith("#"))

    targetSegments.map(_.stripPrefix("#")).mkString("-")
  }
}

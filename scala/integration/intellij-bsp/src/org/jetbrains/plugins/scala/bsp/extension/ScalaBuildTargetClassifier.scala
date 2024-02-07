package org.jetbrains.plugins.scala.bsp.extension

import com.intellij.ide.impl.ProjectUtil
import org.jetbrains.plugins.bsp.extension.points.{BuildTargetClassifierExtension, BuildToolId}
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants

import java.util
import scala.jdk.CollectionConverters._

private class ScalaBuildTargetClassifier extends BuildTargetClassifierExtension {
  override val getBuildToolId: BuildToolId = ScalaPluginConstants.BUILD_TOOL_ID
  override val getSeparator = "/"

  private def getPathSegments(path: String): Option[Array[String]] = {
    val splittedPath = path.split("/").filter(segment => segment.nonEmpty)

    if (splittedPath.nonEmpty) {
      val firstSegment = splittedPath(0)

      if (firstSegment.startsWith("file:")) {
        Option.apply(splittedPath.drop(1))
      } else {
        Option.apply(splittedPath)
      }
    } else {
      None
    }
  }

  private def getBuildTargetWithStrippedPath(buildTarget: String): Array[String] = {
    val maybeSplittedBuildTargetPath = getPathSegments(buildTarget)
    if (maybeSplittedBuildTargetPath.isEmpty) {
      throw new RuntimeException(s"Splitted buildTarget path cannot be empty. buildTarget = [$buildTarget]")
    }
    val splittedBuildTargetPath = maybeSplittedBuildTargetPath.get

    Option.apply(ProjectUtil.getActiveProject)
      .flatMap(currentProject => getPathSegments(currentProject.getBasePath))
      .filter(projectPathSegments => projectPathSegments.nonEmpty)
      .map(projectPathSegments => splittedBuildTargetPath.drop(projectPathSegments.length - 1))
      .getOrElse(splittedBuildTargetPath)
  }

  override def calculateBuildTargetPath(buildTarget: String): util.List[String] = {
    val buildTargetSegments = getBuildTargetWithStrippedPath(buildTarget)

    val (nonTargetSegments, _) = buildTargetSegments.span(segment => !segment.startsWith("#"))
    nonTargetSegments.toList.asJava
  }

  override def calculateBuildTargetName(buildTarget: String): String = {
    val buildTargetSegments = getBuildTargetWithStrippedPath(buildTarget)
    val (_, targetSegments) = buildTargetSegments.span(segment => !segment.startsWith("#"))

    if (targetSegments.length == 1) {
      targetSegments(0).stripPrefix("#")
    } else {
      targetSegments.map(segment => segment.stripPrefix("#")).mkString("-")
    }
  }
}

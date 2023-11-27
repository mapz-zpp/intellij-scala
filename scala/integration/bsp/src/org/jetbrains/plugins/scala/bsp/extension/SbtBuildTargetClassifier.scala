package org.jetbrains.plugins.scala.bsp.extension

import org.jetbrains.plugins.bsp.extension.points.BspBuildTargetClassifierExtension

import java.util
import scala.jdk.CollectionConverters._

private object SbtBuildTargetClassifier extends BspBuildTargetClassifierExtension {
  override val name: String = "sbtBsp"

  override val separator: String = "/"

  private val sbtLabelRegex = """@?@?(?<repository>.*)//(?<package>.*):(?<target>.*)""".r

  override def getBuildTargetPath(s: String): util.List[String] =
    sbtLabelRegex.findFirstMatchIn(s)
      .map {
        _.group("package")
          .split("/")
          .filter {
            _.nonEmpty
          }
      }
      .getOrElse(Array.empty[String])
      .toList
      .asJava

  override def getBuildTargetName(s: String): String =
    sbtLabelRegex.findFirstMatchIn(s)
      .map {
        _.group("target")
      }
      .getOrElse(s)
}


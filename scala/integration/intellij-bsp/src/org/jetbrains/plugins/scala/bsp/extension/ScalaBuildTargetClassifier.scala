package org.jetbrains.plugins.scala.bsp.extension

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.{Project, ProjectManager}
import com.intellij.openapi.roots.ProjectRootManager
import org.jetbrains.plugins.bsp.extension.points.{BuildTargetClassifierExtension, BuildToolId}
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants
import org.jetbrains.plugins.scala.bsp.server.connection.ScalaConnectionDetailsProviderService

import java.util
import scala.jdk.CollectionConverters._

private class ScalaBuildTargetClassifier extends BuildTargetClassifierExtension {
  private val logger = Logger.getInstance(this.getClass)

  private val buildTargetFileRegex = """(file:/)(.*)/(#[^/]*/[^/]*)""".r
  private val buildTargetHiddenFileRegex = """(file:/+)([^/]+/.*)""".r
  override val getBuildToolId: BuildToolId = ScalaPluginConstants.BUILD_TOOL_ID
  override val getSeparator = "/"

  override def calculateBuildTargetPath(buildTarget: String): util.List[String] = {
    buildTarget match {
      case buildTargetFileRegex(_, path, _) =>
        val projects = ProjectManager.getInstance().getOpenProjects.filter(p => p.getBasePath.contains(path))
        if (projects.length > 1) {
          logger.warn(s"buildTargetClassifier found more than one project with provided path ($path)")
          path.split("/").toList.asJava
        }
        val tmp = projects.head.getActualComponentManager.getService(classOf[ScalaConnectionDetailsProviderService]).getState.projectRootPath
        if (tmp.isEmpty){
          path.split("/").toList.asJava
        }

        tmp.get.getParent.toString match {
          case buildTargetHiddenFileRegex(_, toHidePath) =>
            path.replace(toHidePath, "").stripPrefix("/").split("/").toList.asJava
          case _ =>
            List.empty.asJava
        }
      case _ =>
        logger.warn(s"buildTargetFileRegex mismatch, s=$buildTarget")
        List.empty.asJava
    }
  }

  override def calculateBuildTargetName(buildTarget: String): String = {
    buildTarget match {
      case buildTargetFileRegex(_, _, name) =>
        name
      case _ =>
        logger.warn(s"buildTargetFileRegex mismatch, s=$buildTarget")
        ""
    }
  }
}

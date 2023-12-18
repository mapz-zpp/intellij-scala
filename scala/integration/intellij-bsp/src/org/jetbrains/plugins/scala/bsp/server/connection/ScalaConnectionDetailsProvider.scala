package org.jetbrains.plugins.scala.bsp.server.connection

import ch.epfl.scala.bsp4j.BspConnectionDetails
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlin.coroutines.Continuation
import org.jetbrains.plugins.bsp.extension.points.BuildToolId
import org.jetbrains.plugins.bsp.server.connection.ConnectionDetailsProviderExtension
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants.BUILD_TOOL_ID

import java.lang
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters._

class ScalaConnectionDetailsProvider extends ConnectionDetailsProviderExtension {
  override def onFirstOpening(project: Project, projectPath: VirtualFile, continuation: Continuation[_ >: lang.Boolean]): AnyRef = {
    val connectionFile = getConnectionFile(projectPath)
    if (connectionFile.isDefined) {
      continuation.resumeWith(true)
      return boolean2Boolean(true)
    }

    val hasGeneratedFile = generateSbtConnectionFile(projectPath).isDefined
    continuation.resumeWith(hasGeneratedFile)
    boolean2Boolean(hasGeneratedFile)
  }

  override def provideNewConnectionDetails(project: Project, bspConnectionDetails: BspConnectionDetails): BspConnectionDetails =
    bspConnectionDetails // For now return current details (Don't know when to generate new ones)

  override def getBuildToolId: BuildToolId = BUILD_TOOL_ID

  private def getConnectionFile(projectPath: VirtualFile): Option[VirtualFile] = {
    getChild(projectPath, List.apply(".bsp", "sbt.json"))
  }

  private def generateSbtConnectionFile(projectPath: VirtualFile): Option[VirtualFile] = {
    val process = new ProcessBuilder("coursier", "launch", "sbt", "--", "bspConfig")
      .directory(projectPath.toNioPath.toFile)
      .start

    if (process.waitFor(2, TimeUnit.MINUTES)) {
      if (process.exitValue() != 0) {
        val processInput = process.inputReader.lines.toList.asScala.mkString
        val processError = process.errorReader.lines.toList.asScala.mkString
        throw new RuntimeException(s"Sbt bsp file genration with coursier failed. stdout=[$processInput], stderr=[$processError]")
      }

      getConnectionFile(projectPath)
    } else {
      throw new RuntimeException("Sbt bsp file generation timed out")
    }
  }

  private def getChild(root: VirtualFile, path: List[String]): Option[VirtualFile] = {
    val found = path.foldLeft(Option(root)) { (vf, child) =>
      vf.flatMap(vf => {
        vf.refresh(false, false)
        Option(vf.findChild(child))
      }
      )
    }

    found.map(vf => {
      vf.refresh(false, false)
      vf
    })
  }
}

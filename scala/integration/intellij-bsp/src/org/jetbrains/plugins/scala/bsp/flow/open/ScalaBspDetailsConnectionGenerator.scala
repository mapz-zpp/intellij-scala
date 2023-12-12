package org.jetbrains.plugins.scala.bsp.flow.open

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.bsp.config.BspPluginBundle
import org.jetbrains.plugins.bsp.flow.open.wizard.ImportProjectWizardStep
import org.jetbrains.plugins.bsp.ui.console.BspConsoleService
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants

import java.io.OutputStream
import java.nio.file.Path
import java.util
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters._

class ScalaBspDetailsConnectionGenerator  {
  def id(): String = ScalaPluginConstants.ID

  def displayName(): String = "Scala"

  def canGenerateBspConnectionDetailsFile(projectPath: VirtualFile): Boolean =
    true // for now we assume that this check is redundant since it is already done earlier

  def generateBspConnectionDetailsFile(projectPath: VirtualFile, outputStream: OutputStream, project: Project): VirtualFile = {
    val process = new ProcessBuilder("coursier", "launch", "sbt", "--", "bspConfig")
      .directory(projectPath.toNioPath.toFile)
      .start
    process.waitFor(2, TimeUnit.MINUTES)

    getChild(projectPath, List(".bsp", "sbt.json").asJava)
  }

  def calculateImportWizardSteps(path: Path, observableMutableProperty: ObservableMutableProperty[Any]): util.List[ImportProjectWizardStep] = List.empty.asJava

  def executeAndWait(command: util.List[String], projectPath: VirtualFile, outputStream: OutputStream, project: Project): Unit = {
    val commandStr = command.asScala.concat(" ").toString()
    val bspSyncConsole = new BspConsoleService(project).getBspSyncConsole
    bspSyncConsole.addMessage(BspPluginBundle.message("console.task.run.message.command", commandStr))

    val builder = new ProcessBuilder()
      .directory(projectPath.toNioPath.toFile)
      .redirectError(ProcessBuilder.Redirect.PIPE)

    val consoleProcess = builder.start()
    consoleProcess.getInputStream.transferTo(outputStream)
    consoleProcess.getErrorStream.transferTo(outputStream)
    consoleProcess.waitFor()

    if (consoleProcess.exitValue() != 0) {
      throw new RuntimeException("Error occurred while running command: " + commandStr)
    }
  }

  def getChild(root: VirtualFile, path: util.List[String]): VirtualFile = {
    val pathAsScala = path.asScala
    val found = pathAsScala.foldLeft(root) { (vf, child) =>
      vf.refresh(false, false)
      vf.findChild(child)
    }
    found.refresh(false, false)
    found
  }
}

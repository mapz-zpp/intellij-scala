package org.jetbrains.plugins.scala.project.bsp.sbt.module

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.{ModuleConfigurationState, ModuleElementsEditor}
import org.jetbrains.plugins.scala.bsp.config.ScalaPluginConstants
import org.jetbrains.plugins.scala.project.bsp.sbt.data.SbtBuildModuleInfo
import org.jetbrains.sbt.SbtBundle
import org.jetbrains.sbt.project.module.SbtModule.Resolvers
import org.jetbrains.sbt.resolvers.SbtResolver
import scala.jdk.CollectionConverters._

import javax.swing.JPanel
import javax.swing.table.AbstractTableModel

private class SbtBspModuleSettingsEditor(state: ModuleConfigurationState) extends ModuleElementsEditor(state) {

  private val myForm = new SbtBspModuleSettingsForm
  private val resolvers = Resolvers(getModel.getModule).toSeq

  override def getDisplayName: String = ScalaPluginConstants.SBT_TAB_NAME

  override def saveData(): Unit = {}

  override def createComponentImpl(): JPanel = myForm.getMainPanel

  override def reset(): Unit = {
    val sbtVersion = SbtBuildModuleInfo.sbtBuildTarget.map(_.getSbtVersion).getOrElse("unknown")
    val imports: java.util.List[String] = SbtBuildModuleInfo.sbtBuildTarget match {
      case Some(target) => target.getAutoImports
      case None => java.util.List.of()
    }
    val resolversModel = new ResolversModel(resolvers, state.getProject)

    myForm.reset(sbtVersion, imports, resolversModel)
  }
}

private class ResolversModel(val resolvers: Seq[SbtResolver], val project:Project) extends AbstractTableModel {

  private val columns = Seq(
    SbtBundle.message("sbt.settings.resolvers.name"),
    SbtBundle.message("sbt.settings.resolvers.url")
  )

  override def getColumnCount: Int = columns.size

  override def getRowCount: Int = resolvers.size

  override def getColumnName(columnIndex: Int): String = columns(columnIndex)

  override def getValueAt(rowIndex: Int, columnIndex: Int): String = {
    val valueOpt = columnIndex match {
      case 0 => resolvers.lift(rowIndex).map(_.name)
      case 1 => resolvers.lift(rowIndex).map(_.root)
      case _ => Some("")
    }
    valueOpt.getOrElse("???")
  }
}


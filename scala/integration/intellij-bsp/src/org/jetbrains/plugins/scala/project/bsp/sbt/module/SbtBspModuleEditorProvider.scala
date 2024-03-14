package org.jetbrains.plugins.scala.project.bsp.sbt.module

import com.intellij.openapi.module.ModuleConfigurationEditor
import com.intellij.openapi.roots.ui.configuration._

class SbtBspModuleEditorProvider extends ModuleConfigurationEditorProvider {
  override def createEditors(state: ModuleConfigurationState): Array[ModuleConfigurationEditor] =
    state.getCurrentRootModel.getModule.getName match {
      case name if name.contains("build") =>
        Array(new SbtBspModuleSettingsEditor(state))
      case _ => Array()
    }
}

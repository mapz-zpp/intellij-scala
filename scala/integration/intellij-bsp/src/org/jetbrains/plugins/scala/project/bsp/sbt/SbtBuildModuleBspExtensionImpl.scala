package org.jetbrains.plugins.scala.project.bsp.sbt

import ch.epfl.scala.bsp4j.{BuildTargetIdentifier, SbtBuildTarget}
import com.intellij.openapi.externalSystem.model.{DataNode, ProjectSystemId}
import com.intellij.openapi.module.Module
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider
import org.jetbrains.plugins.bsp.sbt.SbtBuildModuleBspExtension
import org.jetbrains.plugins.scala.project.bsp.sbt.data.SbtBuildModuleInfo

class SbtBuildModuleBspExtensionImpl extends SbtBuildModuleBspExtension {
  @Override
  def enrichBspSbtModule(sbtBuildTarget: SbtBuildTarget, baseDirectory: String, systemId: ProjectSystemId, buildTargetId: BuildTargetIdentifier, modelsProvider: IdeModifiableModelsProvider): Unit = {
    SbtBuildModuleInfo.sbtBuildTarget = Some(sbtBuildTarget)
    SbtBuildModuleInfo.sbtBuildTargetId = Some(buildTargetId)
  }
}



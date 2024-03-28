package org.jetbrains.plugins.scala.project.bsp.sbt

import ch.epfl.scala.bsp4j.{BuildTargetIdentifier, SbtBuildTarget}
import com.intellij.openapi.components.{PersistentStateComponent, Service, State, Storage, StoragePathMacros}
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.bsp.sbt.SbtBuildModuleBspExtension

class SbtBuildModuleBspExtensionImpl extends SbtBuildModuleBspExtension {
  @Override
  def enrichBspSbtModule(sbtBuildTarget: SbtBuildTarget, buildTargetId: BuildTargetIdentifier, project: Project): Unit = {
    saveProjectRootFile(project, List((sbtBuildTarget, buildTargetId)))
  }

  private def saveProjectRootFile(project: Project, sbtBuildModuleInfos: List[(SbtBuildTarget, BuildTargetIdentifier)]): Unit = {
    project.getActualComponentManager
      .getService(classOf[SbtBuildModuleBspExtensionService])
      .loadState(SbtBuildModuleBspExtensionMetadata(sbtBuildModuleInfos))
  }
}

object SbtBuildModuleBspExtensionImpl {
  def getSbtBuildTargetIds(project: Project): List[(SbtBuildTarget, BuildTargetIdentifier)] = {
    project.getActualComponentManager.getService(classOf[SbtBuildModuleBspExtensionService]).getState.sbtBuildModuleInfos
  }
}

case class SbtBuildModuleBspExtensionMetadata(var sbtBuildModuleInfos: List[(SbtBuildTarget, BuildTargetIdentifier)])

@State(
  name = "SbtBuildModuleBspExtensionService",
  storages = Array(new Storage(StoragePathMacros.MODULE_FILE)),
  reportStatistic = true
)
@Service(Array(Service.Level.PROJECT))
final class SbtBuildModuleBspExtensionService extends PersistentStateComponent[SbtBuildModuleBspExtensionMetadata] {
  private var sbtBuildModuleInfos: List[(SbtBuildTarget, BuildTargetIdentifier)] = List.empty

  override def getState: SbtBuildModuleBspExtensionMetadata =
    SbtBuildModuleBspExtensionMetadata(sbtBuildModuleInfos)

  override def loadState(state: SbtBuildModuleBspExtensionMetadata): Unit =
    this.sbtBuildModuleInfos = state.sbtBuildModuleInfos
}

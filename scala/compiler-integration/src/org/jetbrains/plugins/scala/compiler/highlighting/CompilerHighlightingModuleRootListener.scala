package org.jetbrains.plugins.scala.compiler.highlighting

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.{ModuleRootEvent, ModuleRootListener}

private final class CompilerHighlightingModuleRootListener(project: Project) extends ModuleRootListener {
  override def rootsChanged(event: ModuleRootEvent): Unit = {
    if (event.isCausedByWorkspaceModelChangesOnly) {
      // The rootsChanged event is fired multiple times after project sync. Checking for
      // `isCausedByWorkspaceModelChangesOnly` makes sure that we do not trigger multiple compilations.
      TriggerCompilerHighlightingService.get(project).triggerCompilationInSelectedEditor()
    }
  }
}

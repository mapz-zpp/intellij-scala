package org.jetbrains.plugins.scala.compiler

import com.intellij.openapi.compiler.{CompileContext, CompileTask}

class UnlockCompilerTask
  extends CompileTask {

  override def execute(context: CompileContext): Boolean = {
    CompilerLock.unlock(context.getProject)
    true
  }
}

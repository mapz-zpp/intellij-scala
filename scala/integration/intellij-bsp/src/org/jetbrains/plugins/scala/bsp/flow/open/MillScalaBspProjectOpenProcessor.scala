package org.jetbrains.plugins.scala.bsp.flow.open

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.bsp.flow.open.BaseBspProjectOpenProcessor
import org.jetbrains.plugins.scala.bsp.config.MillScalaPluginConstants

class MillScalaBspProjectOpenProcessor extends BaseBspProjectOpenProcessor(MillScalaPluginConstants.BUILD_TOOL_ID) {
  override def getName: String = "Mill over BSP"

  override def canOpenProject(projectPath: VirtualFile): Boolean =
    projectPath != null && projectPath.findChild(MillScalaPluginConstants.MILL_CONFIG_FILE) != null
}


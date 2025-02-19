package org.jetbrains.plugins.scala.compiler.zinc

import com.intellij.openapi.module.{Module, ModuleManager}
import com.intellij.testFramework.CompilerTester
import org.jetbrains.plugins.scala.CompilationTests
import org.jetbrains.plugins.scala.compiler.data.IncrementalityType
import org.jetbrains.plugins.scala.project.settings.ScalaCompilerConfiguration
import org.junit.Assert.{assertNotNull, assertNull}
import org.junit.experimental.categories.Category

import scala.jdk.CollectionConverters._

@Category(Array(classOf[CompilationTests]))
class SharedSourcesCrossProjectTest extends ZincTestBase {

  private var middleJS: Module = _
  private var middleJVM: Module = _
  private var middleShared: Module = _

  private var baseJS: Module = _
  private var baseJVM: Module = _
  private var baseShared: Module = _

  override def setUp(): Unit = {
    super.setUp()

    createProjectSubDirs(
      "project",
      "base/js/src/main/scala", "base/jvm/src/main/scala", "base/shared/src/main/scala",
      "middle/js/src/main/scala", "middle/jvm/src/main/scala", "middle/shared/src/main/scala"
    )
    createProjectSubFile("project/build.properties", "sbt.version=1.9.7")
    createProjectSubFile("project/plugins.sbt",
    """addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.13.2")
      |addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
      |""".stripMargin)
    createProjectSubFile("base/jvm/src/main/scala/Foo.scala", "class FooJVM")
    createProjectSubFile("base/js/src/main/scala/Foo.scala", "class FooJS")
    createProjectSubFile("middle/shared/src/main/scala/Shared.scala", "class Shared")
    createProjectConfig(
      """lazy val root = project.in(file("."))
        |
        |lazy val middle = crossProject(JVMPlatform, JSPlatform).in(file("middle"))
        |
        |lazy val base = crossProject(JVMPlatform, JSPlatform).in(file("base"))
        |  .dependsOn(middle)
        |""".stripMargin)

    importProject(false)
    ScalaCompilerConfiguration.instanceIn(myProject).incrementalityType = IncrementalityType.SBT

    val modules = ModuleManager.getInstance(myProject).getModules
    middleJS = modules.find(_.getName == "root.middle.middleJS").orNull
    assertNotNull("Could not find module with name 'root.middle.middleJS'", middleJS)
    middleJVM = modules.find(_.getName == "root.middle.middleJVM").orNull
    assertNotNull("Could not find module with name 'root.middle.middleJVM'", middleJVM)
    middleShared = modules.find(_.getName == "root.middle.middle-sources").orNull
    assertNotNull("Could not find module with name 'root.middle.middle-sources'", middleShared)
    baseJS = modules.find(_.getName == "root.base.baseJS").orNull
    assertNotNull("Could not find module with name 'root.base.baseJS'", baseJS)
    baseJVM = modules.find(_.getName == "root.base.baseJVM").orNull
    assertNotNull("Could not find module with name 'root.base.baseJVM'", baseJVM)
    baseShared = modules.find(_.getName == "root.base.base-sources").orNull
    assertNotNull("Could not find module with name 'root.base.base-sources'", baseShared)
    compiler = new CompilerTester(myProject, java.util.Arrays.asList(modules: _*), null, false)
  }

  def testSharedSourcesOnlyCompiledToOwnerModules(): Unit = {
    val messages1 = compiler.make().asScala.toSeq
    assertNoErrorsOrWarnings(messages1)

    Seq(middleJS, middleJVM).foreach { module =>
      val sharedClass = compiler.findClassFile("Shared", module)
      assertNotNull(s"Shared class file not found in ${module.getName}", sharedClass)
    }

    Seq(baseJS, baseJVM).foreach { module =>
      val sharedClass = compiler.findClassFile("Shared", module)
      assertNull(s"Shared class file found in ${module.getName}, but it shouldn't", sharedClass)
    }
  }
}

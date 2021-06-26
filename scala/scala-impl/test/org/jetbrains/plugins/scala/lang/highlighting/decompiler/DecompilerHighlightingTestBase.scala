package org.jetbrains.plugins.scala
package lang
package highlighting
package decompiler

import com.intellij.psi.PsiDocumentManager
import org.jetbrains.plugins.scala.DependencyManagerBase.RichStr
import org.jetbrains.plugins.scala.annotator.{AnnotatorHolderMock, Error, Message, ScalaAnnotator}
import org.jetbrains.plugins.scala.base.ScalaFixtureTestCase
import org.jetbrains.plugins.scala.base.libraryLoaders.{IvyManagedLoader, LibraryLoader}
import org.jetbrains.plugins.scala.decompiler.DecompilerTestBase
import org.jetbrains.plugins.scala.extensions.PsiElementExt
import org.jetbrains.plugins.scala.util.assertions.MatcherAssertions

/**
  * @author Roman.Shein
  * @since 31.05.2016.
  */
abstract class DecompilerHighlightingTestBase extends ScalaFixtureTestCase with DecompilerTestBase with MatcherAssertions {
  override protected def supportedIn(version: ScalaVersion): Boolean = version  >= LatestScalaVersions.Scala_2_11

  override protected val includeReflectLibrary: Boolean = true
  override protected val includeCompilerAsLibrary: Boolean = true

  override def basePath = s"${super.basePath}/highlighting/"

  override def doTest(fileName: String): Unit = {
    assertNothing(getMessages(fileName, decompile(getClassFilePath(fileName))))
  }

  def getMessages(fileName: String, scalaFileText: String): List[Message] = {
    myFixture.configureByText(
      fileName.substring(0, fileName.lastIndexOf('.')) + ".scala",
      scalaFileText.replace("{ /* compiled code */ }", "???")
    )

    PsiDocumentManager.getInstance(getProject).commitAllDocuments()

    implicit val mock: AnnotatorHolderMock = new AnnotatorHolderMock(getFile)
    val annotator = ScalaAnnotator.forProject

    getFile.depthFirst().foreach(annotator.annotate)

    mock.annotations.filter {
      case Error(_, null) | Error(null, _) => false
      case Error(_, message)               => !message.contains("Auxiliary constructor must begin with call to 'this'")
      case _                               => false
    }
  }
}

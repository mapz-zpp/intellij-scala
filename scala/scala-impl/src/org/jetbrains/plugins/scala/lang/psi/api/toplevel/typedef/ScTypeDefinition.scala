package org.jetbrains.plugins.scala.lang.psi.api.toplevel
package typedef

import com.intellij.navigation.NavigationItem
import com.intellij.psi._
import com.intellij.psi.impl.PsiClassImplUtil
import org.jetbrains.plugins.scala.extensions._
import org.jetbrains.plugins.scala.lang.psi.adapters.PsiClassAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaElementVisitor
import org.jetbrains.plugins.scala.lang.psi.api.statements._
import org.jetbrains.plugins.scala.lang.psi.api.statements.params.ScTypeParam
import org.jetbrains.plugins.scala.lang.psi.types.PhysicalMethodSignature

trait ScTypeDefinition extends ScTemplateDefinition
  with ScMember
  with ScMember.WithBaseIconProvider
  with NavigationItem
  with PsiClassAdapter
  with ScTypeParametersOwner
  with ScDocCommentOwner
  with ScCommentOwner {

  //name of additional class generated by scalac
  def additionalClassJavaName: Option[String] = None

  /**
   * Is this a case class or an enum case.
   */
  def isCase: Boolean = false

  def isObject: Boolean = false

  def getPath: String = {
    val qualName = qualifiedName
    val index = qualName.lastIndexOf('.')
    if (index < 0) "" else qualName.substring(0, index)
  }

  def getQualifiedNameForDebugger: String

  def methodsByName(name: String): Iterator[PhysicalMethodSignature]

  def isPackageObject = false

  override protected def acceptScala(visitor: ScalaElementVisitor): Unit = {
    visitor.visitTypeDefinition(this)
  }

  def getSourceMirrorClass: PsiClass

  override def isEquivalentTo(another: PsiElement): Boolean = {
    PsiClassImplUtil.isClassEquivalentTo(this, another)
  }

  def allInnerTypeDefinitions: Seq[ScTypeDefinition] = this.membersWithSynthetic.filterByType[ScTypeDefinition]

  def typeParameters: Seq[ScTypeParam]

  //TODO: add ScalaDoc: what is it, how it's different from baseCompanion?
  def fakeCompanionModule: Option[ScObject]

  override def showAsInheritor: Boolean = true

  def baseCompanion: Option[ScTypeDefinition]
}

object ScTypeDefinition {

  implicit class ScTypeDefinitionExt(private val target: ScTypeDefinition) extends AnyVal {
    def shortDefinition: String =
      keywordPrefix + target.name

    def keywordPrefix: String =
      target match {
        case _: ScObject => "object "
        case _: ScTrait  => "trait "
        case _: ScClass  => "class "
        case _: ScEnum   => "enum "
        case _: ScGiven   => "given "
        case _           => ""
      }
  }
}
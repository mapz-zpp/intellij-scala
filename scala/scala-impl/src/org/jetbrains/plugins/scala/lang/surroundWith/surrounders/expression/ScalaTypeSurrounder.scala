package org.jetbrains.plugins.scala.lang.surroundWith.surrounders.expression

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.lang.psi.api.base.types.ScTypeElement
import org.jetbrains.plugins.scala.lang.psi.api.expr._
import org.jetbrains.plugins.scala.lang.psi.types.result._

class ScalaTypeSurrounder extends ScalaExpressionSurrounder {
  override def getTemplateAsString(elements: Array[PsiElement]): String = {
    val expression = elements(0).asInstanceOf[ScExpression]
    val result = expression.`type`().getOrAny
    s"(${super.getTemplateAsString(elements)}: ${result.presentableText(expression)})"
  }

  //noinspection ScalaExtractStringToBundle,DialogTitleCapitalization
  override def getTemplateDescription: String = "(expr: Type)"

  override def isApplicable(elements: Array[PsiElement]): Boolean = {
    if (elements.length != 1) return false
    elements(0) match {
      case _: ScExpression => true
      case _ => false
    }
  }

  override def getSurroundSelectionRange(editor: Editor, withType: ASTNode): TextRange = {
    lazy val defaultRange = {
      val expr: ScExpression = withType.getPsi.asInstanceOf[ScExpression]
      val offset = expr.getTextRange.getEndOffset
      new TextRange(offset, offset)
    }

    unwrapParenthesis(withType) match {
      case Some(typedExpr: ScTypedExpression) =>
        typedExpr.typeElement match {
          case Some(te: ScTypeElement) =>
            if (te.textMatches("Any"))
              te.getTextRange
            else
              defaultRange
          case _ => defaultRange
        }
      case _ => defaultRange
    }
  }
}

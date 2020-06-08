package org.jetbrains.plugins.scala.annotator.intention

import java.awt.Point

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.util.Key

trait PopupPosition {
  def showPopup(popup: JBPopup, editor: Editor): Unit
}

object PopupPosition {
  def best: PopupPosition = _.showInBestPositionFor(_)

  def at(point: Point): PopupPosition = (popup, editor) => popup.showInScreenCoordinates(editor.getComponent, point)

  def atCustomLocation: PopupPosition =  { (popup, editor) =>
    val customPoint = customLocationKey.get(editor)
    if (customPoint != null) {
      popup.showInScreenCoordinates(editor.getComponent, customPoint)
    }
  }

  def withCustomPopupLocation(editor: Editor, point: Point)(action: => Unit): Unit = {
    customLocationKey.set(editor, point)
    try
      action
    finally
      customLocationKey.set(editor, null)
  }

  private val customLocationKey: Key[Point] = Key.create("popup.custom.location")
}

package b9

import b9.CssSettings._
import b9.components.MetaIDE
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import shared.Apps

import scala.scalajs.js.annotation.JSExportTopLevel

object ModelerApp {
  @JSExportTopLevel("main")
  def main(args: Array[String]): Unit = {
    ModelerCss.addToDocument()
    init(
      dom.document.getElementById(Apps.ModelerApp),
      dom.document.getElementById(Apps.EditorApp),
      dom.document.getElementById(Apps.ViewApp)
    )
  }

  def init(graphEle: Element, editorEle: Element, viewEle: Element): Unit = {
    val dispatcher = new Dispatcher[ModelerState](ModelerOps.initialModel)

    MetaIDE(dispatcher).renderIntoDOM(graphEle)
  }
}

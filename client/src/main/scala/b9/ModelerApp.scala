package b9

import b9.CssSettings._
import b9.TreeOps.TTN
import b9.components.graph.TreeGraph.GraphState
import b9.components.MetaIDE
import japgolly.scalajs.react.Callback
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
    val (tree, meta) = TreeOps.initialModel
    val treeDispatcher = new Dispatcher[(TTN, Callback)](tree, Callback.empty)
    val rootId = tree.rootLabel.uuid
    val graphDispatcher = new Dispatcher[(GraphState, Callback)](GraphState(rootId, rootId, rootId, Set.empty), Callback.empty)

    MetaIDE(treeDispatcher, graphDispatcher, meta).renderIntoDOM(graphEle)
  }
}

package b9

import b9.CssSettings._
import b9.TreeOps.TTN
import b9.components.MetaIDE
import b9.components.graph.TreeGraph.GraphState
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import shared.Apps

import scala.scalajs.js.annotation.JSExportTopLevel

object ModelerApp {
  @JSExportTopLevel("main")
  def main(args: Array[String]): Unit = {
    ModelerCss.addToDocument()
    init(
      dom.document.getElementById(Apps.ModelerApp)
    )
  }

  def init(graphEle: Element): Unit = {
    val (tree, meta) = TreeOps.initialModel
    val treeDispatcher = new Dispatcher[TTN](tree)
    val rootId = tree.rootLabel.uuid
    val graphDispatcher = new Dispatcher[GraphState](GraphState(rootId, rootId, rootId, Set.empty))

    MetaIDE(treeDispatcher, graphDispatcher, meta).renderIntoDOM(graphEle)
  }
}

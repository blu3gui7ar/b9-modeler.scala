package b9

import b9.CssSettings._
import b9.components.editor.Editor
import b9.components.graph.TreeGraph
import japgolly.scalajs.react._
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import shared.Apps

object ModelerApp {
  def require(): Unit = {
    WebpackRequire.React
    WebpackRequire.ReactDOM
    ()
  }

  def main(args: Array[String]): Unit = {
    require()
    ModelerCss.addToDocument()
    init(
      dom.document.getElementById(Apps.ModelerApp),
      dom.document.getElementById(Apps.EditorApp)
    )
  }

  def init(graphEle: Element, editorEle: Element): Unit = {
    import japgolly.scalajs.react.vdom.Implicits._

    val modelerConnection = ModelerCircuit.connect(s => s.graph)

    modelerConnection(p => TreeGraph(p, 700, 500))
      .renderIntoDOM(graphEle)
    //    val dc = ModelerCircuit.wrap(_.graph)(TreeGraph(_, 700, 500))
    //    dc.renderIntoDOM(ele)

    modelerConnection(p => Editor(p.zoom(_.display)))
      .renderIntoDOM(editorEle)
  }
}

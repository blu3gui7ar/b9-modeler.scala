package b9

import b9.CssSettings._
import b9.components.editor.Editor
import b9.components.graph.TreeGraph
import b9.components.json.JsonView
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import shared.Apps

object ModelerApp {
  def main(args: Array[String]): Unit = {
    ModelerCss.addToDocument()
    init(
      dom.document.getElementById(Apps.ModelerApp),
      dom.document.getElementById(Apps.EditorApp),
      dom.document.getElementById(Apps.ViewApp)
    )
  }

  def init(graphEle: Element, editorEle: Element, viewEle: Element): Unit = {
    import japgolly.scalajs.react.vdom.Implicits._

    val modelerConnection = ModelerCircuit.connect(s => s.graph)

    modelerConnection(p => TreeGraph(p, 700, 500))
      .renderIntoDOM(graphEle)
    //    val dc = ModelerCircuit.wrap(_.graph)(TreeGraph(_, 700, 500))
    //    dc.renderIntoDOM(ele)

    modelerConnection(p => Editor(p.zoom(_.display)))
      .renderIntoDOM(editorEle)

    modelerConnection(p => JsonView(p.zoom(_.root)))
      .renderIntoDOM(viewEle)
  }
}

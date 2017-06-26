package b9

import b9.CssSettings._
import b9.components.TreeGraph
import japgolly.scalajs.react._
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import shared.Apps

import scala.scalajs.js

object ModelerApp extends js.JSApp {

  sealed abstract class Action

  case object Add extends Action

  case object Sub extends Action

  def require(): Unit = {
    WebpackRequire.React
    WebpackRequire.ReactDOM
    ()
  }

  def main(): Unit = {
    require()
    ModelerCss.addToDocument()
    init(dom.document.getElementById(Apps.ModelerApp))
  }

  def init(ele: Element): Unit = {
    import japgolly.scalajs.react.vdom.Implicits._
    val modelerConnection = ModelerCircuit.connect(s => s.graph)
    val c = modelerConnection(p => TreeGraph(p, 700, 500))
    c.renderIntoDOM(ele)
  }
}

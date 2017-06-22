package b9

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
    init(dom.document.getElementById(Apps.ModelerApp))
  }

  def joints(root: TreeNode): Seq[TreeNode] = {
    val children = root.children
    Seq(root) ++ children.map(joints(_)).flatten
  }


  def init(ele: Element): Unit = {
    import japgolly.scalajs.react.vdom.Implicits._

    //    val reader: ModelRO[CaseNode] = ModelerCircuit.zoom({ r =>
    //      println(r)
    //      r.tree
    //    })
    //    println(reader())

    val modelerConnection = ModelerCircuit.connect(s => joints(s.tree))

    val c = modelerConnection(p => TreeGraph(p, 700, 500))
    c.renderIntoDOM(ele)
  }
}

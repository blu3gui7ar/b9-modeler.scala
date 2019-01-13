package b9

import b9.short.TM
import play.api.libs.json._
import scalaz.Tree.Node
import ModelerOps._

case class ValueAddAction(node: TM, ref: String, value: JsValue) extends Action {
  override def transfer: ModelerState => ModelerState =  { state =>
    val display = state.graph.display
    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
      loc.modifyTree { case Node(rootLabel, subForest) =>
        val newV: Seq[JsValue] = rootLabel.value match {
          case arr: JsArray => value +: arr.value
          case _ => Seq.empty
        }
        Node(rootLabel.copy(value = JsArray(newV)), subForest)
      }
    } map { newNodeLoc =>
      val newDp = findParent(newNodeLoc, display.tree)
      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
      state.copy(graph = newGraph)
    } getOrElse(state)
  }
}

case class ValueDelAction(node: TM, ref: String, value: JsValue) extends Action {
  override def transfer: ModelerState => ModelerState = { state =>
    val display = state.graph.display
    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
      loc.modifyTree { case Node(rootLabel, subForest) =>
        val newV: Seq[JsValue] = rootLabel.value match {
          case arr: JsArray => arr.value.filter(_ != value)
          case _ => Seq.empty
        }
        Node(rootLabel.copy(value = JsArray(newV)), subForest)
      }
    } map { newNodeLoc =>
      val newDp = findParent(newNodeLoc, display.tree)
      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
      state.copy(graph = newGraph)
    } getOrElse (state)
  }
}

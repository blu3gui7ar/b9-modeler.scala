package b9

import diode.ActionResult.ModelUpdate
import diode.react.ReactConnector
import diode.{ActionHandler, Circuit, ModelRW}
import facades.d3js.Hierarchy
import meta.TreeNode

import scala.scalajs.js

/**
  * Created by blu3gui7ar on 2017/6/24.
  */
object ModelerCircuit extends Circuit[State] with ReactConnector[State] {
  type TN = IdNode[TreeNode]

//  protected def syncPos(tn: TN): TreeNode = {
//    tn.data.map( n =>
//      if (tn.x.map(_ != n.x).getOrElse(true) || tn.y.map(_ != n.y).getOrElse(true)) {
//        if (tn.diffDescendants.map(_ > 1).getOrElse(false)) {
//          val nc: js.Array[TreeNode] = tn.children.map(_.map(syncPos(_))).getOrElse(Empty)
//          n.copy(x = tn.x.getOrElse(n.x), y = tn.y.getOrElse(n.y), children = nc)
//        }
//        else n.copy(x = tn.x.getOrElse(n.x), y = tn.y.getOrElse(n.y))
//      } else {
//        if (tn.diffDescendants.map(_ > 0).getOrElse(false)) {
//          val nc: js.Array[TreeNode] = tn.children.map(_.map(syncPos(_))).getOrElse(Empty)
//          n.copy(children = nc)
//        }
//        else n
//      }
//    ).getOrElse(TreeExtractor.Empty)
//  }

  object layout {
    val width = 700
    val left = 50
    val right = 200

    val height = 500
    val top = 10
    val bottom = 10

    val tree = {
      val t = Hierarchy.tree()
      t.size(js.Array(height - top - bottom, width - left - right))
      t
    }

    def apply(node: TN): TN = tree(node)
//    def apply(node: TN): TN = tree(node.count().sort((a: TN, b: TN) =>
//        (b.value.getOrElse(0.0) - a.value.getOrElse(0.0)).toInt))

    def gather(node: TN): TN = {
      val x = (width - left - right) / 2 + left
      val y = (height - top - bottom) / 2  + top

      node.eachBefore { n: TN =>
        n.x = x.toDouble
        n.y = y.toDouble
      }
    }
  }

  def relocate(node: TN): TN = {
    //rebuild depth
    val dn = node.eachBefore { n: TN =>
      n.depth = n.parent.toOption match {
        case Some(null) => 0
        case Some(parent) => if(n == node) 0 else parent.depth.getOrElse(0) + 1
        case None => 0
      }
    }
    layout(dn)

    //count diff nodes
//    node.eachAfter { n: TN =>
//      val v: Int = n.data.map(d => {
//        if (n.x.map(_ == d.x).getOrElse(false) && n.y.map(_ == d.y).getOrElse(false)) 0
//        else 1
//      }).getOrElse(0)
//
//      n.diffDescendants = v + n.children.map(_.map(_.diffDescendants.getOrElse(0)).fold(0)(_ + _)).getOrElse(0)
//    }
  }


  protected var idx: Int = 0
  def reindex(node: TN): TN = {
    idx = 0
    node.eachBefore { n: TN =>
      n.id = nextIdx()
    }
  }
  def nextIdx(): Int = {
    idx += 1
    idx
  }

  protected def init(root: TreeNode) : TN = {
    import js.JSConverters._
    val hroot = Hierarchy.hierarchy[TreeNode, TN](root, {n => n.children.toJSArray}: js.Function1[TreeNode, js.Array[TreeNode]])
    layout.gather(reindex(hroot))
  }

  override protected def initialModel: State = {
    val r = init(Sample.tree())
    State(GraphState(r, r, r, None, None))
  }

  override protected def actionHandler: ModelerCircuit.HandlerFunction = new ModelerActionHandler(ModelerCircuit.zoomTo(s => s.graph))

  class ModelerActionHandler[M](modelRW: ModelRW[M, GraphState]) extends ActionHandler(modelRW) {
    override def handle  = {
      case DisplayFromAction(node) =>
        ModelUpdate(modelRW.zoomTo(_.displayRoot).updated(relocate(node)))
      case EditAction(node) =>
        ModelUpdate(modelRW.zoomTo(_.editingNode).updated(Some(node)))
      case ActiveAction(node) =>
        ModelUpdate(modelRW.zoomTo(_.activeNode).updated(Some(node)))
    }
  }
}

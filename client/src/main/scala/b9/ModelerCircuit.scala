package b9

import diode.ActionResult.{ModelUpdate, NoChange}
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

    def apply[N](node: IdNode[N]): IdNode[N] = tree(node)

    def gather[N](node: IdNode[N], x: Double, y: Double): IdNode[N] =
      node.eachBefore { n: IdNode[N] =>
        n.x = x
        n.y = y
      }

    def gather[N](node: IdNode[N]): IdNode[N] =
      gather(node, (width - left - right) / 2 + left, (height - top - bottom) / 2  + top)
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

  def rehierarchy(root: TN, tree: TN) : TN = {
    tree.eachBefore(_.display = false)
    val empty = js.Array[TN]()
    val rhroot = Hierarchy.hierarchy[TN, IdNode[TN]](root, {n => if(n.fold.getOrElse(false)) empty else n.children.getOrElse(empty) }: js.Function1[TN, js.Array[TN]])
    (layout(rhroot) eachBefore { n: IdNode[TN] =>
      n.data.toOption match {
        case Some(rn) => {
          rn.x = n.x
          rn.y = n.y
          rn.display = true
        }
        case _ =>
      }
    })
    .data.getOrElse(root)
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
      case DisplayFromAction(node) => {
        val tree = modelRW.zoom(_.tree)
        ModelUpdate(modelRW.zoomTo(_.displayRoot).updated(rehierarchy(node, tree())))
      }
      case FoldAction(node) => {
        if (node.children.map(_.nonEmpty).getOrElse(false)) {
          //direct update is not right
          val fold = !node.fold.getOrElse(false)
          node.fold = fold
          val tree = modelRW.zoom(_.tree)
          val droot = modelRW.zoom(_.displayRoot)
          val rhroot = rehierarchy(droot(), tree())
          if (fold) layout.gather(node, node.x.getOrElse(0.0), node.y.getOrElse(0.0))
          ModelUpdate(modelRW.zoomTo(_.displayRoot).updated(rhroot))
        } else NoChange
      }
      case EditAction(node) =>
        ModelUpdate(modelRW.zoomTo(_.editingNode).updated(Some(node)))
      case ActiveAction(node) =>
        ModelUpdate(modelRW.zoomTo(_.activeNode).updated(Some(node)))
    }
  }
}
